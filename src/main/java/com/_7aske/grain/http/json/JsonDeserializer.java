package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.util.Pair;
import com._7aske.grain.util.iterator.IndexedStringIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDeserializer {
	private final IndexedStringIterator iterator;

	public JsonDeserializer(String content) {
		this.iterator = new IndexedStringIterator(content);
	}

	private Pair<String, Object> parseEntry() {
		iterator.eatWhitespace();
		if (!iterator.peek().equals("\"")) {
			throw new JsonDeserializationException("Expected '\"' " + iterator.getInfo());
		}
		String key = iterator.eatKey();
		iterator.eatWhitespace();
		if (!iterator.peek().equals(":")) {
			throw new JsonDeserializationException("Expected ':' " + iterator.getInfo());
		} else {
			iterator.next();
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
		while (true) {
			if (iterator.hasNext() && iterator.peek().equals("]")) {
				iterator.next();
				break;
			}

			iterator.eatWhitespace();
			Object val = parseValue();
			iterator.eatWhitespace();
			list.add(val);
			if (iterator.peek().equals(",")) {
				iterator.next();
				iterator.eatWhitespace();
			} else if (iterator.peek().equals("]")) {
				iterator.next();
				iterator.eatWhitespace();
				break;
			}
		}

		return list;
	}

	private Object parseObject() {
		Map<String, Object> obj = new HashMap<>();
		iterator.next(); // skip '{'
		while (true) {
			if (iterator.hasNext() && iterator.peek().equals("}")) {
				iterator.next();
				break;
			}
			Pair<String, Object> kv = parseEntry();
			obj.put(kv.getFirst(), kv.getSecond());
			if (iterator.peek().equals(","))
				iterator.next();
			else
				break;
		}
		iterator.eatWhitespace();
		if (!iterator.peek().equals("}")) {
			throw new JsonDeserializationException("Expected '}' at " + iterator.getInfo());
		} else {
			iterator.next();
			iterator.eatWhitespace();
			return obj;
		}
	}

	public JsonObject parse() {
		Map<String, Object> json = new HashMap<>();
		iterator.eatWhitespace();
		if (!iterator.peek().equals("{")) {
			throw new JsonDeserializationException("Expected '{' " + iterator.getInfo());
		} else {
			iterator.next();
		}

		while (iterator.hasNext()) {
			Pair<String, Object> kv = parseEntry();
			json.put(kv.getFirst(), kv.getSecond());

			iterator.eatWhitespace();

			if (iterator.hasNext() && iterator.peek().equals(",")) {
				iterator.next();
			} else if (iterator.hasNext() && (iterator.peek().equals("}"))) {
				break;
			} else {
				throw new JsonDeserializationException("Expected '}' at " + iterator.getInfo());
			}
		}

		return new JsonObject(json);
	}

}
