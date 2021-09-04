package com._7aske.grain.http.json;

import com._7aske.grain.http.HttpHeaders;
import com._7aske.grain.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public final class JsonResponse<T> {
	private HttpStatus status;
	private Map<String, String> headers;
	private JsonObject body;

	private JsonResponse() {
		this.status = HttpStatus.OK;
		this.headers = new HashMap<>();
		this.headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
	}

	private JsonResponse(T object) {
		JsonDeserializer<T> deserializer = new JsonDeserializer<>((Class<T>) object.getClass());
		this.body = deserializer.deserialize(object);
		this.status = HttpStatus.OK;
		this.headers = new HashMap<>();
		this.headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
	}

	public static <T> JsonResponse<T> ok(T object) {
		JsonResponse<T> tJsonResponse = new JsonResponse<>(object);
		tJsonResponse.status = HttpStatus.OK;
		return tJsonResponse;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public JsonObject getBody() {
		return body;
	}

	public HttpStatus getStatus() {
		return status;
	}

	private static final class Builder<T> {
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

		public JsonResponse<T> body(T object) {
			JsonDeserializer<T> deserializer = new JsonDeserializer<>((Class<T>) object.getClass());
			instance.body = deserializer.deserialize(object);
			return instance;
		}
	}
}
