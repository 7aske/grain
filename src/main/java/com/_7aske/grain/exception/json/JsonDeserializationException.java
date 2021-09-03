package com._7aske.grain.exception.json;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpStatus;

public class JsonDeserializationException extends HttpException {
	public JsonDeserializationException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}
