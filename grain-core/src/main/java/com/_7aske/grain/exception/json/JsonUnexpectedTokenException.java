package com._7aske.grain.exception.json;

import com._7aske.grain.util.iterator.IteratorPositionInformation;

public class JsonUnexpectedTokenException extends JsonDeserializationException {
	public JsonUnexpectedTokenException(int codePoint, IteratorPositionInformation info) {
		super(String.format("Unexpected token '%c' %s", codePoint, info));
	}

	public JsonUnexpectedTokenException(String token, IteratorPositionInformation info) {
		super(String.format("Unexpected token '%s' %s", token, info));
	}

	public JsonUnexpectedTokenException(int codePoint, String expected, IteratorPositionInformation info) {
		super(String.format("Unexpected token '%c' %s. Expected '%s'", codePoint, info, expected));
	}
}
