package com._7aske.grain.web.server;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpParsingException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.ArrayUtil;
import com._7aske.grain.util.StringUtils;
import com._7aske.grain.web.http.GrainHttpRequest;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.session.Cookie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static com._7aske.grain.web.http.ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
import static com._7aske.grain.web.http.HttpConstants.*;
import static com._7aske.grain.web.http.HttpHeaders.*;

public class HttpRequestReader implements AutoCloseable {
	private static final Logger log = LoggerFactory.getLogger(HttpRequestReader.class);
	private final BufferedInputStream reader;
	private static final Pattern QUERY_PARAMS_DELIMITER_REGEX = Pattern.compile("\\?");
	private static final Pattern URL_ENCODED_VALUE_LIST_SEPARATOR_REGEX = Pattern.compile("\\s*,\\s*");
	private static final Pattern URL_ENCODED_VALUE_SEPARATOR = Pattern.compile("=");
	private static final Pattern URL_ENCODED_KEY_SEPARATOR = Pattern.compile("&");
	private static final Pattern HEADER_PARAMETER_SEPARATOR_REGEX = Pattern.compile("\\s*,\\s*");
	private static final Pattern HEADER_SEPARATOR_REGEX = Pattern.compile(":\\s*");
	private static final Pattern REQUEST_LINE_SEPARATOR = Pattern.compile(" ");

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
			buffer.appendCodePoint(c);
		} while (reader.available() > 0 && !isStartOfBody(buffer));

		int crlfIndex = buffer.indexOf(CRLF);
		if (crlfIndex == -1) {
			throw new HttpParsingException("Invalid HTTP request line");
		}


		parseRequestLine(buffer, crlfIndex, request);

		parseHeaders(buffer, crlfIndex, request);

		Charset encoding = Optional.ofNullable(request.getCharacterEncoding())
				.map(Charset::forName)
				.orElse(Charset.defaultCharset());

		// Now that we have encoding information we can put parameters
		request.putParameters(parseParameters(request.getQueryString(), request.getCharacterEncoding()));

		long contentLength = request.getContentLength();
		if (contentLength > 0 && (Objects.equals(request.getHeader(CONTENT_TYPE), APPLICATION_X_WWW_FORM_URLENCODED))) {
				String body = new String(reader.readNBytes((int) contentLength), encoding);
				request.putParameters(parseParameters(body, request.getCharacterEncoding()));
		}



		return request;
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


	private void parseHeaders(StringBuilder buffer, int firstCrLfIndex, GrainHttpRequest request) {
		String headers = buffer.substring(firstCrLfIndex + CRLF_LEN, buffer.length() - CRLF_LEN);
		for (String header : headers.split(CRLF)) {
			String[] headerParts = HEADER_SEPARATOR_REGEX.split(header, 2);
			if (headerParts.length != 2) {
				log.warn("Invalid header: {}", header);
				continue;
			}

			String headerName = headerParts[0].trim();
			String headerValue = headerParts[1].trim();
			String[] valueParts = HEADER_PARAMETER_SEPARATOR_REGEX.split(headerValue);
			String actualValue = valueParts[0];

			// Not really a special case but a charset optional parameter in the
			// Content-Type header can influence the encoding of the request body.
			// It overrides the encoding specified by the Content-Encoding header.
			if (headerName.equals(CONTENT_TYPE)) {
				for (int i = 1; i < valueParts.length; i++) {
					String[] paramParts = URL_ENCODED_VALUE_SEPARATOR.split(valueParts[i], 2);
					if (paramParts.length != 2) {
						continue;
					}

					if (paramParts[0].equals(CHARSET)) {
                        try {
                            request.setCharacterEncoding(paramParts[1]);
                        } catch (UnsupportedEncodingException e) {
                            throw new GrainRuntimeException("Unrecognized charset " + paramParts[1], e);
                        }
                    }
				}
			}

			// @Refactor is this okay?
			// only if not already set by Content-Type charset parameter
			if (headerName.equals(CONTENT_ENCODING) && (!actualValue.equals(StandardCharsets.ISO_8859_1.name()))) {
					try {
						request.setCharacterEncoding(actualValue);
					} catch (UnsupportedEncodingException e) {
						throw new GrainRuntimeException("Unrecognized charset " + actualValue, e);
					}

			}

			request.setHeader(headerName, actualValue);

		}

		if (request.hasHeader(COOKIE)) {
			for (Map.Entry<String, Cookie> kv : Cookie.parse(request.getHeader(COOKIE)).entrySet()) {
				request.addCookie(kv.getValue());
			}
		}
	}

	private boolean isStartOfBody(StringBuilder buffer) {
		// ends in \r\n\r\n - start of body segment
		return buffer.codePointAt(buffer.length() - 1) == CRLF_BYTES[1] &&
				buffer.codePointAt(buffer.length() - 2) == CRLF_BYTES[0] &&
				buffer.codePointAt(buffer.length() - 3) == CRLF_BYTES[1] &&
				buffer.codePointAt(buffer.length() - 4) == CRLF_BYTES[0];
	}

	@Override
	public void close() throws IOException {
		reader.close();
    }
}
