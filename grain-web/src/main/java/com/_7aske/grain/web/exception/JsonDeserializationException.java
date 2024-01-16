package com._7aske.grain.web.exception;

import static com._7aske.grain.web.http.HttpStatus.BAD_REQUEST;

public class JsonDeserializationException extends HttpException {
	public JsonDeserializationException(String message) {
		super(message, BAD_REQUEST);
	}
}
