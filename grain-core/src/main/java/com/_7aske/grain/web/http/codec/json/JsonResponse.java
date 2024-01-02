package com._7aske.grain.web.http.codec.json;

import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpHeaders;
import com._7aske.grain.web.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public final class JsonResponse<T> {
	private HttpStatus status;
	private Map<String, String> headers;
	private T body;

	private JsonResponse() {
		this.status = HttpStatus.OK;
		this.headers = new HashMap<>();
		this.headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON);
	}

	private JsonResponse(T object) {
		this.body = object;
		this.status = HttpStatus.OK;
		this.headers = new HashMap<>();
		this.headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON);
	}

	public static <T> JsonResponse<T> ok(T object) {
		JsonResponse<T> tJsonResponse = new JsonResponse<>(object);
		tJsonResponse.status = HttpStatus.OK;
		return tJsonResponse;
	}

	public static <T> Builder<T> body(T object) {
		return new Builder<T>().body(object);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public T getBody() {
		return body;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static final class Builder<T> {
		private final JsonResponse<T> instance;

		public Builder() {
			this.instance = new JsonResponse<>();
		}

		public Builder<T> status(HttpStatus status) {
			instance.status = status;
			return this;
		}

		public Builder<T> header(String header, String value) {
			if (instance.headers == null)
				instance.headers = new HashMap<>();
			instance.headers.put(header, value);
			return this;
		}

		public Builder<T> body(T object) {
			instance.body = object;
			return this;
		}

		public JsonResponse<T> build() {
			return instance;
		}
	}
}
