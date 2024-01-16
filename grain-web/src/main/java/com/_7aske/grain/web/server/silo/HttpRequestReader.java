package com._7aske.grain.web.server.silo;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.exception.HttpParsingException;
import com._7aske.grain.util.ArrayUtil;
import com._7aske.grain.util.ByteBuffer;
import com._7aske.grain.util.StringUtils;
import com._7aske.grain.web.http.GrainHttpRequest;
import com._7aske.grain.web.http.HttpHeader;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.RequestParams;
import com._7aske.grain.web.http.multipart.PartImpl;
import com._7aske.grain.web.http.session.Cookie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com._7aske.grain.web.http.ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
import static com._7aske.grain.web.http.ContentType.MULTIPART_FORM_DATA;
import static com._7aske.grain.web.http.HttpConstants.*;
import static com._7aske.grain.web.http.HttpHeaders.*;

public class HttpRequestReader implements AutoCloseable {
    private final BufferedInputStream reader;
    private final ByteBuffer buffer;
    public static final Pattern QUERY_PARAMS_DELIMITER_REGEX = Pattern.compile("\\?");
    public static final Pattern HEADER_PARAMETER_SEPARATOR_REGEX = Pattern.compile("\\s*;\\s*");
    public static final Pattern HEADER_SEPARATOR_REGEX = Pattern.compile(":\\s*");
    public static final Pattern REQUEST_LINE_SEPARATOR = Pattern.compile(" ");

    public HttpRequestReader(BufferedInputStream bufferedInputStream) {
        this.reader = bufferedInputStream;
        this.buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    }


    public GrainHttpRequest readHttpRequest() throws IOException {
        buffer.resize(reader.available());

        GrainHttpRequest request = new GrainHttpRequest();
        request.setScheme(HTTP);

        // request line
        buffer.writeUntil(reader, b -> !b.endsWith(CRLF_BYTES));
        parseRequestLine(buffer.toString(), request);
        buffer.reset();


        buffer.writeUntil(reader, b -> !b.endsWith(CRLFCRLF_BYTES));
        Map<String, HttpHeader> headers = parseHeaders(buffer.toString());
        buffer.reset();

        for (Map.Entry<String, HttpHeader> entry : headers.entrySet()) {
            if (entry.getKey().equals(COOKIE)) {
                for (Map.Entry<String, Cookie> kv : Cookie.parse(entry.getValue().getValue()).entrySet()) {
                    request.addCookie(kv.getValue());
                }
            } else if (entry.getKey().equals(CONTENT_TYPE)) {
                entry.getValue().getOptionalParameter(BOUNDARY).ifPresent(request::setBoundary);
                entry.getValue().getOptionalParameter(CHARSET).ifPresent(enc -> {
                    try {
                        request.setCharacterEncoding(enc);
                    } catch (UnsupportedEncodingException e) {
                        throw new GrainRuntimeException("Unrecognized charset " + enc, e);
                    }
                });
            } else if (entry.getKey().equals(CONTENT_ENCODING) && (!entry.getValue().getValue().equals(StandardCharsets.ISO_8859_1.name()))) {
                try {
                    request.setCharacterEncoding(entry.getValue().getValue());
                } catch (UnsupportedEncodingException e) {
                    throw new GrainRuntimeException("Unrecognized charset " + entry.getValue().getValue(), e);
                }
            }
            request.setHeader(entry.getKey(), entry.getValue().getValue());
        }

        Charset encoding = Optional.ofNullable(request.getCharacterEncoding())
                .map(Charset::forName)
                .orElse(Charset.defaultCharset());

        // Now that we have encoding information we can put parameters
        request.putParameters(RequestParams.parse(request.getQueryString(), request.getCharacterEncoding()).getParameters());

        long contentLength = request.getContentLength();
        if (contentLength > 0 && (Objects.equals(request.getHeader(CONTENT_TYPE), APPLICATION_X_WWW_FORM_URLENCODED))) {
            String body = new String(reader.readNBytes((int) contentLength), encoding);
            request.putParameters(RequestParams.parse(body, request.getCharacterEncoding()).getParameters());
        } else if (contentLength > 0 && Objects.equals(request.getHeader(CONTENT_TYPE), MULTIPART_FORM_DATA)) {
            parseMultipart(request, reader);
        } else if (contentLength > 0) {
            while (contentLength-- > 0) {
                OutputStream requestOutputStream = request.getOutputStream();
                requestOutputStream.write(reader.read());
            }
        }

        return request;
    }

    private void parseMultipart(GrainHttpRequest request, BufferedInputStream reader) throws IOException {
        if (StringUtils.isBlank(request.getBoundary())) {
            throw new HttpParsingException("Missing boundary in multipart/form-data request");
        }

        byte[] boundary = request.getBoundary().getBytes();
        byte[] boundaryCrlf = (request.getBoundary() + CRLF).getBytes();

        // Allocate to some sane initial size. Reader.available() returns
        // an estimated (not accurate) amount of bytes available.
        buffer.resize(reader.available());

        buffer.writeN(reader, boundaryCrlf.length);
        if (!ArrayUtil.equals(buffer, boundaryCrlf)) {
            throw new HttpParsingException("Body doesn't start with the specified boundary");
        }
        buffer.reset();

        while (reader.available() > 0) {
            // read headers
            buffer.writeUntil(reader, b -> !b.endsWith(CRLFCRLF_BYTES));
            Map<String, HttpHeader> headers = parseHeaders(buffer.toString());
            buffer.reset();

            // read body
            buffer.writeUntil(reader, b -> !b.endsWith(boundary));
            buffer.setLength(buffer.length() - boundary.length); // trim back boundary
            if (buffer.endsWith(CRLF_BYTES)) {
                buffer.setLength(buffer.length() - CRLF_LEN);
            }

            PartImpl part = PartImpl.builder()
                    .withName(Optional.ofNullable(headers.get(CONTENT_DISPOSITION))
                            .flatMap(cd -> cd.getOptionalParameter(NAME))
                            .orElse(null))
                    .withFileName(Optional.ofNullable(headers.get(CONTENT_DISPOSITION))
                            .flatMap(cd -> cd.getOptionalParameter(FILENAME))
                            .orElse(null))
                    .withContentType(Optional.ofNullable(headers.get(CONTENT_TYPE))
                            .map(HttpHeader::getValue)
                            .orElse(null))
                    .withHttpHeaders(headers)
                    .withContent(buffer.getBytes(), buffer.length())
                    .build();

            request.addPart(part);
            buffer.reset();

            // next two bytes if they exists are either -- or \r\n
            buffer.writeN(reader, 2);
            if (buffer.endsWith(BOUNDARY_SEP_BYTES)) {
                break;
            }

            buffer.reset();


        }

    }

    private void parseRequestLine(String requestLineString, GrainHttpRequest request) {
        String[] requestLineParts = REQUEST_LINE_SEPARATOR.split(requestLineString);


        if (requestLineParts.length != 3) {
            throw new HttpParsingException();
        }

        request.setMethod(HttpMethod.resolve(requestLineParts[0]));


        String[] pathParts = QUERY_PARAMS_DELIMITER_REGEX.split(requestLineParts[1]);
        if (pathParts.length == 2) {
            request.setQueryString(pathParts[1]);
        }
        request.setPath(pathParts[0]);
        request.setVersion(requestLineParts[2].trim()); // trim \r\n
    }


    private Map<String, HttpHeader> parseHeaders(String headersString) {
        return Arrays.stream(headersString.split(CRLF))
                .map(HttpHeader::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(HttpHeader::getName, Function.identity()));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
