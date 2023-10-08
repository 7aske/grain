package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.exception.json.JsonUnexpectedTokenException;
import com._7aske.grain.http.json.nodes.*;
import com._7aske.grain.util.Pair;

import java.math.BigDecimal;

public class JsonParser {
	private JsonParserIterator iter;
	private final int[] VALID_NUMBER_TOKENS = new int[]{
			'-', '+', 'e', 'E', '.'
	};

	private Pair<String, JsonNode> parseEntry() {
		iter.eatWhitespace();
		if (!iter.isPeek('"')) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		String key = iter.eatKey();
		iter.eatWhitespace();

		if (!iter.isPeek(':')) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
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
					throw new JsonUnexpectedTokenException(token, iter.getInfo());
            default -> parseOther();
        };
	}

	private JsonNode parseOther() {
		iter.eatWhitespace();

		if (iter.isPeek('t')) {
			String value = iter.eatWord();
			if (!value.equals("true")) {
				throw new JsonUnexpectedTokenException(value, iter.getInfo());
			}

			return new JsonBooleanNode(Boolean.TRUE);
		}

		if (iter.isPeek('f')) {
			String value = iter.eatWord();
			if (!value.equals("false")) {
				throw new JsonUnexpectedTokenException(value, iter.getInfo());
			}

			return new JsonBooleanNode(Boolean.FALSE);
		}

		if (iter.isPeek('n')) {
			String value = iter.eatWord();
			if (!value.equals("null")) {
				throw new JsonUnexpectedTokenException(value, iter.getInfo());
			}

			return JsonNullNode.INSTANCE;
		}

		StringBuilder intPart = new StringBuilder();
		StringBuilder decPart = new StringBuilder();
		StringBuilder expPart = new StringBuilder();
		boolean isNegative = false;
		boolean isExponent = false;
		boolean isDecimal = false;

		while (iter.hasNext()) {

			if (!isValidNumber(iter.peek())) {
				break;
			}

			int curr = iter.next();

			if (curr == '-') {
				if (isNegative) {
					throw new JsonUnexpectedTokenException(curr, iter.getInfo());
				}
				isNegative = true;
			}

			if (curr == 'e' || curr == 'E') {
				if (isExponent) {
					throw new JsonUnexpectedTokenException(curr, iter.getInfo());
				}
				isExponent = true;
			}

			if (curr == '.') {
				if (isDecimal) {
					throw new JsonUnexpectedTokenException(curr, iter.getInfo());
				}
				isDecimal = true;
			}

			if (isExponent) {
				expPart.appendCodePoint(curr);
			} else if (isDecimal) {
				decPart.appendCodePoint(curr);
			} else {
				intPart.appendCodePoint(curr);
			}
		}

		if (intPart.isEmpty() || (isDecimal && decPart.isEmpty()) || (isExponent && expPart.isEmpty())) {
			// @Warning This is not a correct location
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		// Json spec validation

		// negative number with leading zeros: -0123
		if (isNegative && !isDecimal && intPart.length() > 2 && intPart.charAt(1) == '0') {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		// decimal part has no numbers: 123.
		if (isDecimal && decPart.length() == 1) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		// zero length int part in decimal: -.123
		if (isNegative && isDecimal && intPart.length() == 1) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		// leading zeros in int part: 000123
		if (intPart.length() > 1 && intPart.charAt(0) == '0') {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		// If it is not any of these it must be a number
		try {
			Number value;

			if (isExponent) {
				value = new BigDecimal(intPart + decPart.toString() + expPart);
			} else if (isDecimal) {
				value = Double.parseDouble(intPart + decPart.toString());
			} else {
				value = Long.parseLong(intPart.toString());
			}

			return new JsonNumberNode(value);
		} catch (NumberFormatException ex) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
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

		throw new JsonUnexpectedTokenException(iter.peek(), "}", iter.getInfo());
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
					throw new JsonUnexpectedTokenException(iter.peek(), "}", iter.getInfo());
				}
			}

			iter.eatWhitespace();
			if (iter.hasNext()) {
				throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
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
						throw new JsonUnexpectedTokenException(iter.peek(), "<value>", iter.getInfo());
					}
				} else if (!iter.isPeek(']')) {
					iter.rewind();
					throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
				}
			}

			// Check if array was closed properly
			if (!iter.isPeek(']')) {
				iter.rewind();
				throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
			}
			iter.next(); // skip ']'

			iter.eatWhitespace();
			if (iter.hasNext()) {
				throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
			}

			return arr;

		}

		JsonNode value = parseValue();
		iter.eatWhitespace();
		if (iter.hasNext()) {
			throw new JsonDeserializationException("Invalid end of Json string" + iter.getInfo());
		}
		return value;
	}

	private boolean isValidNumber(int c) {
		if ('0' <= c && c <= '9') {
			return true;
		}

		for (int valid : VALID_NUMBER_TOKENS) {
			if (c == valid) {
				return true;
			}
		}

		return false;
	}

}
