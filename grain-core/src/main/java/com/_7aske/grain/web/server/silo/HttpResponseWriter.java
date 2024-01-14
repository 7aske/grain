package com._7aske.grain.web.server.silo;

import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpHeaders;
import com._7aske.grain.web.http.HttpStatus;
import com._7aske.grain.web.http.session.Cookie;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

import static com._7aske.grain.web.http.HttpConstants.*;

public class HttpResponseWriter implements AutoCloseable {
	private final OutputStream outputStream;
	private static final String HEADER_SEPARATOR = ": ";
	private static final String HEADER_VALUE_SEPARATOR = ", ";
	private static final String HEADER_PARAMETER_SEPARATOR = "; ";


	public HttpResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

	public void write(GrainHttpResponse response) throws IOException {
		int space = 32;
		outputStream.write(HTTP_V1.getBytes());
		outputStream.write(space);
		outputStream.write(String.valueOf(response.getStatus()).getBytes());
		outputStream.write(space);
		outputStream.write(HttpStatus.valueOf(response.getStatus()).getReason().getBytes());
		outputStream.write(CRLF_BYTES);

		if (!response.getCookies().isEmpty()) {
			String cookieValue = response.getCookies().values()
					.stream()
					.map(Cookie::toString)
					.collect(Collectors.joining(HEADER_PARAMETER_SEPARATOR));
			response.setHeader(HttpHeaders.SET_COOKIE, cookieValue);
		}


		for (String header : response.getHeaderNames()) {
			outputStream.write(header.getBytes());
			outputStream.write(HEADER_SEPARATOR.getBytes());
			outputStream.write(String.join(HEADER_VALUE_SEPARATOR, response.getHeaders(header)).getBytes());
			outputStream.write(CRLF_BYTES);
		}

		outputStream.write(CRLF_BYTES);


		response.writeTo(outputStream);
	}

	@Override
	public void close() throws IOException {
		outputStream.flush();
		outputStream.close();
	}
}
