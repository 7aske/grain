package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
	private final JsonParserIterator iterator;

	public JsonParser(String content) {
		this.iterator = new JsonParserIterator(content);
	}

	private Pair<String, Object> parseEntry() {
		iterator.eatWhitespace();
		if (!iterator.isPeek("\"")) {
			throw new JsonDeserializationException("Expected '\"' " + iterator.getInfo());
		}
		String key = iterator.eatKey();
		iterator.eatWhitespace();
		if (!iterator.isPeek(":")) {
			throw new JsonDeserializationException("Expected ':' " + iterator.getInfo());
		} else {
			iterator.next(); // skip ':'
		}
		iterator.eatWhitespace();
		Object value = parseValue();

		return Pair.of(key, value);
	}

	private Object parseValue() {
		String token = iterator.peek();
		switch (token) {
			case "{":
				return parseObject();
			case "[":
				return parseArray();
			case "\"":
				return parseString();
			default:
				return parseOther();
		}
	}

	private Object parseOther() {
		iterator.eatWhitespace();
		String val = iterator.eatWhile(ch -> !ch.isBlank() && !ch.equals(",") && !ch.equals("}") && !ch.equals("]"));
		if (val.equals("true")) {
			return Boolean.TRUE;
		}
		if (val.equals("false")) {
			return Boolean.FALSE;
		}
		if (val.equals("null")) {
			return null;
		}
		try {
			float parsed = Float.parseFloat(val);
			if (parsed == (int) parsed) {
				return (int) parsed;
			} else {
				return parsed;
			}
		} catch (NumberFormatException ex) {
			throw new JsonDeserializationException("Unexpected token '" + val + "' " + iterator.getInfo());
		}
	}

	private Object parseString() {
		return iterator.eatKey();
	}

	private Object parseArray() {
		List<Object> list = new ArrayList<>();
		iterator.next(); // skip [
		while (!iterator.isPeek("]")) {

			iterator.eatWhitespace();
			Object val = parseValue();
			iterator.eatWhitespace();
			list.add(val);
			if (iterator.isPeek(",")) {
				iterator.next();
				iterator.eatWhitespace();
			}
		}
		iterator.next();
		iterator.eatWhitespace();

		return list;
	}

	private Object parseObject() {
		Map<String, Object> obj = new HashMap<>();
		iterator.next(); // skip '{'
		while (!iterator.isPeek("}")) {
			Pair<String, Object> kv = parseEntry();
			obj.put(kv.getFirst(), kv.getSecond());
			if (iterator.isPeek(","))
				iterator.next();
		}
		iterator.eatWhitespace();
		if (iterator.isPeek("}")) {
			iterator.next(); // skip '}'
			iterator.eatWhitespace();
			return obj;
		}

		throw new JsonDeserializationException("Expected '}' " + iterator.getInfo());
	}

	public JsonObject parse() {
		Map<String, Object> json = new HashMap<>();
		iterator.eatWhitespace();
		if (iterator.peek().equals("{")) {
			iterator.next(); // skip '{'
		} else {
			throw new JsonDeserializationException("Expected '{' " + iterator.getInfo());
		}

		while (iterator.hasNext()) {
			Pair<String, Object> kv = parseEntry();
			json.put(kv.getFirst(), kv.getSecond());

			iterator.eatWhitespace();

			if (iterator.isPeek(",")) {
				iterator.next();
			} else if (iterator.isPeek("}")) {
				break;
			} else {
				throw new JsonDeserializationException("Expected '}' " + iterator.getInfo());
			}
		}

		return new JsonObject(json);
	}

}
