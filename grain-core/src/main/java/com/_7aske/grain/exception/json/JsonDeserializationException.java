package com._7aske.grain.exception.json;

import com._7aske.grain.exception.http.HttpException;

import static com._7aske.grain.web.http.HttpStatus.BAD_REQUEST;

public class JsonDeserializationException extends HttpException {
	public JsonDeserializationException(String message) {
		super(message, BAD_REQUEST);
	}
}
