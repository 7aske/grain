package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.exception.json.JsonUnexpectedTokenException;
import com._7aske.grain.http.json.nodes.*;
import com._7aske.grain.util.Pair;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;

public class JsonParser {
	private final int[] VALID_NUMBER_TOKENS = new int[]{
			'-', '+', 'e', 'E', '.'
	};
	private static final int MAX_NESTING_DEPTH = 512;
	private static final Pattern LONG_PATTERN = Pattern.compile("[+-]?\\d+");
	private int nestingDepth = 0;
	private JsonParserIterator iter;

	private Pair<String, JsonNode> parseJsonEntry() {
		iter.eatWhitespace();
		if (!iter.isPeek('"')) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		String key = iter.eatString();
		iter.eatWhitespace();

		if (!iter.isPeek(':')) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}

		iter.next(); // skip ':'
		iter.eatWhitespace();

		JsonNode value = parseJsonValue();

		iter.eatWhitespace();

		return Pair.of(key, value);
	}

	private JsonNode parseJsonValue() {
		int token = iter.peek();
        return switch (token) {
            case '{' -> parseJsonObject();
            case '[' -> parseJsonArray();
            case '"' -> parseJsonString();
			case 't', 'f' -> parseJsonBoolean();
			case 'n' -> parseJsonNull();
			case ']', '}', '+' ->
					throw new JsonUnexpectedTokenException(token, iter.getInfo());
            default -> parseJsonNumber();
        };
	}

	private JsonNode parseJsonBoolean() {
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

		throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
	}

	private JsonNode parseJsonNull() {
		if (iter.isPeek('n')) {
			String value = iter.eatWord();
			if (!value.equals("null")) {
				throw new JsonUnexpectedTokenException(value, iter.getInfo());
			}

			return JsonNullNode.INSTANCE;
		}

		return parseJsonNumber();
    }

	private JsonNode parseJsonNumber() {
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
				value = parseBigDecimal(intPart + decPart.toString() + expPart);
			} else if (isDecimal) {
				value = Double.parseDouble(intPart + decPart.toString());
			} else {
				value = parseLong(intPart.toString());
			}

			return new JsonNumberNode(value);
		} catch (NumberFormatException ex) {
			throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
		}
	}

	private JsonNode parseJsonString() {
		return new JsonStringNode(iter.eatString());
	}

	private JsonNode parseJsonArray() {
		validateNestingDepth();

		JsonArrayNode arr = new JsonArrayNode();
		iter.next(); // skip ']'
		while (!iter.isPeek(']')) {

			iter.eatWhitespace();
			JsonNode val = parseJsonValue();
			iter.eatWhitespace();
			arr.add(val);
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

		iter.eatWhitespace();

		if (!iter.isPeek(']')) {
			throw new JsonUnexpectedTokenException(iter.peek(), "]", iter.getInfo());
		}

		iter.next(); // skip ']'
		iter.eatWhitespace();

		return arr;
	}

	private JsonObjectNode parseJsonObject() {
		validateNestingDepth();

		JsonObjectNode obj = new JsonObjectNode();
		iter.next(); // skip '{'
		while (!iter.isPeek('}')) {
			Pair<String, JsonNode> kv = parseJsonEntry();
			obj.put(kv.getFirst(), kv.getSecond());

			if (iter.isPeek(',')) {
				iter.next();
				// Trailing comma check
				if (iter.isPeek('}')) {
					iter.rewind();
					throw new JsonUnexpectedTokenException(iter.peek(), "<key>", iter.getInfo());
				}

				// Unclosed object check
				if (!iter.hasNext()) {
					throw new JsonDeserializationException("Unexpected end of Json string" + iter.getInfo());
				}
			}
		}

		iter.eatWhitespace();

        if (!iter.isPeek('}')) {
            throw new JsonUnexpectedTokenException(iter.peek(), "}", iter.getInfo());
        }

		iter.next(); // skip '}'
		iter.eatWhitespace();
		return obj;
    }

	public JsonNode parse(String content) {
		this.iter = new JsonParserIterator(content);

		iter.eatWhitespace();

		try {

			if (iter.peek() == '{') {
				JsonObjectNode obj = parseJsonObject();
				if (iter.hasNext()) {
					throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
				}

				return obj;

			} else if (iter.peek() == '[') {
				JsonNode arr = parseJsonArray();
				if (iter.hasNext()) {
					throw new JsonUnexpectedTokenException(iter.peek(), iter.getInfo());
				}

				return arr;

			}

			JsonNode value = parseJsonValue();
			iter.eatWhitespace();
			if (iter.hasNext()) {
				throw new JsonDeserializationException("Invalid end of Json string" + iter.getInfo());
			}
			return value;
		} catch (NoSuchElementException ignored) {
			throw new JsonDeserializationException("Unexpected end of Json string" + iter.getInfo());
		}
	}

	/**
	 * Parse a string into a BigDecimal. If the number is too big to fit in a
	 * BigDecimal - return infinity.
	 *
	 * @param value string to parse
	 * @return parsed number
	 */
	public Number parseBigDecimal(String value) {
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException ex) {
			if (Objects.equals(ex.getMessage(), "Too many nonzero exponent digits.")) {
				if (value.startsWith("-")) {
					return Double.NEGATIVE_INFINITY;
				} else {
					return Double.POSITIVE_INFINITY;
				}
			}

			throw ex;
		}
	}

	/**
	 * Try to parse the string into a long, if it fails and matches the pattern
	 * for integer numbers that means that it is too big to fit in a long and
	 * then parse it into a BigDecimal.
	 *
	 * @param value string to parse
	 * @return parsed number
	 */
	private Number parseLong(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			if (!LONG_PATTERN.matcher(value).matches()) {
				throw ex; // Otherwise can't fit.
			}
		}

		try {
			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			if (LONG_PATTERN.matcher(value).matches()) {
				return new BigDecimal(value);
			}
			throw ex;
		}
	}


	/**
	 * Validate nesting depth to prevent stack overflow.
	 */
	private void validateNestingDepth() {
		if (nestingDepth++ > MAX_NESTING_DEPTH) {
			throw new JsonDeserializationException("Max nesting depth exceeded");
		}
	}

	/**
	 * Checks whether a provided character is a valid number token.
	 *
	 * @param c character to check
	 * @return true if character is a valid number token
	 */
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
