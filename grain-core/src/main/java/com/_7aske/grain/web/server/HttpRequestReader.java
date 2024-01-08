package com._7aske.grain.web.server;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpParsingException;
import com._7aske.grain.util.ArrayUtil;
import com._7aske.grain.util.ByteBuffer;
import com._7aske.grain.util.StreamUtil;
import com._7aske.grain.util.StringUtils;
import com._7aske.grain.web.http.GrainHttpRequest;
import com._7aske.grain.web.http.HttpHeader;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.multipart.PartImpl;
import com._7aske.grain.web.http.session.Cookie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    public static final Pattern QUERY_PARAMS_DELIMITER_REGEX = Pattern.compile("\\?");
    public static final Pattern URL_ENCODED_VALUE_LIST_SEPARATOR_REGEX = Pattern.compile("\\s*,\\s*");
    public static final Pattern URL_ENCODED_VALUE_SEPARATOR = Pattern.compile("=");
    public static final Pattern URL_ENCODED_KEY_SEPARATOR = Pattern.compile("&");
    public static final Pattern HEADER_PARAMETER_SEPARATOR_REGEX = Pattern.compile("\\s*;\\s*");
    public static final Pattern HEADER_SEPARATOR_REGEX = Pattern.compile(":\\s*");
    public static final Pattern REQUEST_LINE_SEPARATOR = Pattern.compile(" ");

    public HttpRequestReader(BufferedInputStream bufferedInputStream) {
        this.reader = bufferedInputStream;
    }


    public GrainHttpRequest readHttpRequest() throws IOException {
        GrainHttpRequest request = new GrainHttpRequest();
        StringBuilder buffer = new StringBuilder();

        request.setScheme(HTTP);

        int c;
        do {
            c = reader.read();
            if (c == -1) {
                break;
            }
            buffer.appendCodePoint(c);
        } while (!endsWith(buffer, CRLF + CRLF));

        int crlfIndex = buffer.indexOf(CRLF);
        if (crlfIndex == -1) {
            throw new HttpParsingException("Invalid HTTP request line");
        }


        parseRequestLine(buffer, crlfIndex, request);

        String headersString = buffer.substring(crlfIndex + CRLF_LEN, buffer.length() - CRLF_LEN);
        Map<String, HttpHeader> headers = parseHeaders(headersString);

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
        request.putParameters(parseParameters(request.getQueryString(), request.getCharacterEncoding()));

        long contentLength = request.getContentLength();
        if (contentLength > 0 && (Objects.equals(request.getHeader(CONTENT_TYPE), APPLICATION_X_WWW_FORM_URLENCODED))) {
            String body = new String(reader.readNBytes((int) contentLength), encoding);
            request.putParameters(parseParameters(body, request.getCharacterEncoding()));
        } else if (contentLength > 0 && Objects.equals(request.getHeader(CONTENT_TYPE), MULTIPART_FORM_DATA)) {
            parseMultipart(request, reader);
        } else if (contentLength > 0) {
            OutputStream requestOutputStream = request.getOutputStream();
            do {
                c = reader.read();
                if (c == -1) {
                    break;
                }
                requestOutputStream.write(c);
            } while (true);
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
        ByteBuffer buffer = ByteBuffer.allocate(reader.available());

        buffer.writeN(reader, boundaryCrlf.length);
        if (!ArrayUtil.equals(buffer, boundaryCrlf)) {
            throw new HttpParsingException("Body doesn't start with the specified boundary");
        }
        buffer.reset();

        while (reader.available() > 0) {
            // read headers
            buffer.writeUntil(reader, b -> !endsWith(b, CRLFCRLF_BYTES));
            Map<String, HttpHeader> headers = parseHeaders(buffer.toString());
            buffer.reset();

            // read body
            buffer.writeUntil(reader, b -> !endsWith(b, boundary));
            buffer.setLength(buffer.length() - boundary.length); // trim back boundary
            if (endsWith(buffer, CRLF_BYTES)) {
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
            if (endsWith(buffer, BOUNDARY_SEP_BYTES)) {
                break;
            }

            buffer.reset();


        }

    }

    public Map<String, String[]> parseParameters(String queryString, String characterEncoding) {
        Map<String, String[]> parameters = new HashMap<>();
        if (StringUtils.isBlank(queryString)) {
            return new HashMap<>();
        }

        Arrays.stream(URL_ENCODED_KEY_SEPARATOR.split(queryString))
                .map(URL_ENCODED_VALUE_SEPARATOR::split)
                .forEach(kv -> {
                    if (kv.length == 2) {
                        String[] values = URL_ENCODED_VALUE_LIST_SEPARATOR_REGEX
                                .split(URLDecoder.decode(kv[1], Charset.forName(characterEncoding)));
                        if (parameters.containsKey(kv[0])) {
                            String[] existing = parameters.get(kv[0]);
                            String[] updated = ArrayUtil.join(existing, values);
                            parameters.put(kv[0], updated);
                        } else {
                            parameters.put(kv[0], values);
                        }
                    } else {
                        parameters.put(kv[0], new String[]{""});
                    }
                });
        return parameters;
    }

    private void parseRequestLine(StringBuilder buffer, int crlfIndex, GrainHttpRequest request) {
        String requestLineString = buffer.substring(0, crlfIndex);
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
        request.setVersion(requestLineParts[2]);
    }


    private Map<String, HttpHeader> parseHeaders(String headersString) {
        return Arrays.stream(headersString.split(CRLF))
                .map(HttpHeader::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(HttpHeader::getName, Function.identity()));
    }

    private boolean endsWith(ByteBuffer buffer, byte[] end) {
        int bufLen = buffer.length();
        int endLen = end.length;
        if (bufLen < endLen) {
            return false;
        }


        return ArrayUtil.equals(
                buffer, bufLen - endLen, bufLen,
                end, 0, endLen);
    }

    private boolean endsWith(StringBuilder buffer, CharSequence end) {
        int bufLen = buffer.length();
        int endLen = end.length();
        if (bufLen < endLen) {
            return false;
        }

        return StreamUtil.equals(
                buffer.substring(bufLen - endLen).codePoints(),
                end.codePoints()
        );
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
