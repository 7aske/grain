package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.http.json.nodes.*;
import com._7aske.grain.util.Pair;

import java.math.BigDecimal;

public class JsonParser {
	private JsonParserIterator iterator;

	public JsonParser() {
	}

	private Pair<String, JsonNode> parseEntry() {
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
		JsonNode value = parseValue();

		return Pair.of(key, value);
	}

	private JsonNode parseValue() {
		String token = iterator.peek();
        return switch (token) {
            case "{" -> parseObject();
            case "[" -> parseArray();
            case "\"" -> parseString();
            default -> parseOther();
        };
	}

	private JsonNode parseOther() {
		iterator.eatWhitespace();
		String val = iterator.eatWhile(ch ->
				!ch.isBlank() &&
				!ch.equals(",") &&
				!ch.equals("}") &&
				!ch.equals("]"));

		if (iterator.isPeek("t")) {
			return new JsonBooleanNode(Boolean.TRUE);
		}

		if (val.equals("true")) {
			return new JsonBooleanNode(Boolean.TRUE);
		}
		if (val.equals("false")) {
			return new JsonBooleanNode(Boolean.FALSE);
		}

		if (val.equals("null")) {
			// No need to allocate a new object for null values
			return JsonNullNode.INSTANCE;
		}

        try {

            if (val.contains("e") || val.contains("E")) {
                return new JsonNumberNode(new BigDecimal(val));
            } else if (val.contains(".")) {
                return new JsonNumberNode(Double.parseDouble(val));
            } else {
                return new JsonNumberNode(Long.parseLong(val));
            }

        } catch (NumberFormatException ex) {
            throw new JsonDeserializationException("Unexpected token '" + val + "' " + iterator.getInfo());
        }
    }

	private JsonNode parseString() {
		return new JsonStringNode(iterator.eatKey());
	}

	private JsonNode parseArray() {
		JsonArrayNode list = new JsonArrayNode();
		iterator.next(); // skip [
		while (!iterator.isPeek("]")) {

			iterator.eatWhitespace();
			JsonNode val = parseValue();
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

	private JsonObjectNode parseObject() {

		JsonObjectNode obj = new JsonObjectNode();
		iterator.next(); // skip '{'
		while (!iterator.isPeek("}")) {
			Pair<String, JsonNode> kv = parseEntry();
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

	public JsonNode parse(String content) {
		this.iterator = new JsonParserIterator(content);

		iterator.eatWhitespace();

		if (iterator.peek().equals("{")) {
			iterator.next(); // skip '{'

			JsonObjectNode object = new JsonObjectNode();

			while (iterator.hasNext()) {
				iterator.eatWhitespace();

				if (iterator.isPeek("}")) {
					break;
				}

				Pair<String, JsonNode> kv = parseEntry();
				object.put(kv.getFirst(), kv.getSecond());

				iterator.eatWhitespace();

				if (iterator.isPeek(",")) {
					iterator.next();
				} else if (iterator.isPeek("}")) {
					break;
				} else {
					throw new JsonDeserializationException("Expected '}' " + iterator.getInfo());
				}
			}

			return object;

		} else if (iterator.peek().equals("[")) {
			iterator.next(); // skip '['

			JsonArrayNode array = new JsonArrayNode();

			// @Refactor
			while (iterator.hasNext()) {
				iterator.eatWhitespace();

				if (iterator.isPeek("]")) {
					break;
				}

				JsonNode value = parseValue();
				array.add(value);

				iterator.eatWhitespace();

				if (iterator.isPeek(",")) {
					iterator.next();
				} else if (iterator.isPeek("]")) {
					break;
				} else {
					throw new JsonDeserializationException("Expected ']' or ',' " + iterator.getInfo());
				}
			}

			return array;

		}

		return parseValue();
//		throw new JsonDeserializationException("Invalid start of Json string" + iterator.getInfo());
	}

}
