package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.http.json.nodes.*;
import com._7aske.grain.util.Pair;

import java.math.BigDecimal;

public class JsonParser {
	private JsonParserIterator iter;

	private Pair<String, JsonNode> parseEntry() {
		iter.eatWhitespace();
		if (!iter.isPeek('"')) {
			throw new JsonDeserializationException("Expected '\"' " + iter.getInfo());
		}

		String key = iter.eatKey();
		iter.eatWhitespace();

		if (!iter.isPeek(':')) {
			throw new JsonDeserializationException("Expected ':' " + iter.getInfo());
		} else {
			iter.next(); // skip ':'
		}
		iter.eatWhitespace();
		JsonNode value = parseValue();

		iter.eatWhitespace();

		return Pair.of(key, value);
	}

	private JsonNode parseValue() {
		int token = iter.peek();
        return switch (token) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
			case ']', '}', '+' ->
					throw new JsonDeserializationException("Unexpected '" + token + "' " + iter.getInfo());
            default -> parseOther();
        };
	}

	private JsonNode parseOther() {
		iter.eatWhitespace();

		if (iter.isPeek('t')) {
			String value = iter.eatWord();
			if (!value.equals("true")) {
				throw new JsonDeserializationException("Unexpected token '" + value + "'" + iter.getInfo());
			}

			return new JsonBooleanNode(Boolean.TRUE);
		}

		if (iter.isPeek('f')) {
			String value = iter.eatWord();
			if (!value.equals("false")) {
				throw new JsonDeserializationException("Unexpected token '" + value + "'" + iter.getInfo());
			}

			return new JsonBooleanNode(Boolean.FALSE);
		}

		if (iter.isPeek('n')) {
			String value = iter.eatWord();
			if (!value.equals("null")) {
				throw new JsonDeserializationException("Unexpected token '" + value + "'" + iter.getInfo());
			}

			return JsonNullNode.INSTANCE;
		}

		StringBuilder sb = new StringBuilder();
		boolean isNegative = false;

		while (iter.hasNext() && !iter.isPeek('}', ']', ',') && !Character.isSpaceChar(iter.peek()) && iter.peek() != '\n') {
			int curr = iter.next();

			if (curr == '-') {
				if (isNegative) {
					throw new JsonDeserializationException("Unexpected token '" + curr +"'" + iter.getInfo());
				}
				isNegative = true;
			}

			sb.appendCodePoint(curr);
		}


		// If it is not any of these it must be a number

		String val = sb.toString();

        try {

            if (val.contains("e") || val.contains("E")) {
				return new JsonNumberNode(new BigDecimal(val));
            } else if (val.contains(".")) {
                return new JsonNumberNode(Double.parseDouble(val));
            } else {
                return new JsonNumberNode(Long.parseLong(val));
            }

        } catch (NumberFormatException ex) {
            throw new JsonDeserializationException("Unexpected token '" + val + "' " + iter.getInfo());
        }
    }

	private JsonNode parseString() {
		return new JsonStringNode(iter.eatKey());
	}

	private JsonNode parseArray() {
		JsonArrayNode arr = new JsonArrayNode();
		iter.next(); // skip ']'
		while (!iter.isPeek(']')) {

			iter.eatWhitespace();
			JsonNode val = parseValue();
			iter.eatWhitespace();
			arr.add(val);
			if (iter.isPeek(',')) {
				iter.next();
				iter.eatWhitespace();
			}
		}
		iter.next(); // skip ']'
		iter.eatWhitespace();

		return arr;
	}

	private JsonObjectNode parseObject() {

		JsonObjectNode obj = new JsonObjectNode();
		iter.next(); // skip '{'
		while (!iter.isPeek('}')) {
			Pair<String, JsonNode> kv = parseEntry();
			obj.put(kv.getFirst(), kv.getSecond());

			if (iter.isPeek(','))
				iter.next();

		}

		iter.eatWhitespace();

		if (iter.isPeek('}')) {
			iter.next(); // skip '}'
			iter.eatWhitespace();
			return obj;
		}

		throw new JsonDeserializationException("Expected '}' " + iter.getInfo());
	}

	public JsonNode parse(String content) {
		this.iter = new JsonParserIterator(content);

		iter.eatWhitespace();

		if (iter.peek() == '{') {
			iter.next(); // skip '{'

			JsonObjectNode obj = new JsonObjectNode();

			while (iter.hasNext()) {
				iter.eatWhitespace();

				if (iter.isPeek('}')) {
					iter.next(); // skip '}'
					break;
				}

				Pair<String, JsonNode> kv = parseEntry();
				obj.put(kv.getFirst(), kv.getSecond());

				iter.eatWhitespace();

				if (iter.isPeek(',')) {
					iter.next();
				} else if (!iter.isPeek('}')) {
					throw new JsonDeserializationException("Expected '}' " + iter.getInfo());
				}
			}

			iter.eatWhitespace();
			if (iter.hasNext()) {
				throw new JsonDeserializationException("Unexpected token '" + iter.peek() + "' " + iter.getInfo());
			}

			return obj;

		} else if (iter.peek() == '[') {
			iter.next(); // skip '['

			JsonArrayNode arr = new JsonArrayNode();

			// @Refactor
			while (iter.hasNext()) {
				iter.eatWhitespace();

				if (iter.isPeek(']')) {
					break;
				}

				JsonNode value = parseValue();
				arr.add(value);

				iter.eatWhitespace();

				if (iter.isPeek(',')) {
					iter.next();
					if (iter.isPeek(']')) {
						iter.prev(); // for more precise error message
						throw new JsonDeserializationException("<value> expected, got ',' " + iter.getInfo());
					}
				} else if (!iter.isPeek(']')) {
					throw new JsonDeserializationException("Expected ']' or ',' " + iter.getInfo());
				}
			}

			// Check if array was closed properly
			if (!iter.isPeek(']')) {
				throw new JsonDeserializationException("',' or ']' expected " + iter.getInfo());
			}
			iter.next(); // skip ']'

			iter.eatWhitespace();
			if (iter.hasNext()) {
				throw new JsonDeserializationException("Unexpected token '" + iter.peek() + "' " + iter.getInfo());
			}


			return arr;

		}

		return parseValue();
//		throw new JsonDeserializationException("Invalid start of Json string" + iterator.getInfo());
	}

}
