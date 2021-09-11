package com._7aske.grain.compiler.lexer;

import com._7aske.grain.util.iterator.IndexedStringIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com._7aske.grain.compiler.lexer.TokenType.*;

public class Lexer extends IndexedStringIterator {
	private int start;
	protected Consumer<Token> callback;
	protected List<Token> tokens;

	public Lexer(String code) {
		super(code);
		this.start = getCharacter();
		this.callback = token -> {
		};
		this.tokens = new ArrayList<>();
	}

	public void onEmit(Consumer<Token> callback) {
		this.callback = callback;
	}

	protected void emit(Token token) {
		this.tokens.add(token);
		this.callback.accept(token);
	}

	public String eatStringLiteral() {
		StringBuilder builder = new StringBuilder();
		int startingAt = getIndex();
		String quoteType = next();
		String curr = null;
		while (hasNext()) {
			curr = next();
			if (curr.equals(quoteType) && !prev().equals("\\")) break;
			builder.append(curr);
		}

		if (!Objects.equals(curr, quoteType)) {
			rewind(getIndex() - startingAt);
			return null;
		}

		return builder.toString();
	}

	private void doLex() {
		while (hasNext()) {
			eatWhitespace();
			start = getCharacter();


			if (isStartOfIdentifier()) {
				String val = eatWord();
				TokenType kwOrIden = classifyToken(val);
				Token token = createToken(kwOrIden, val);
				emit(token);
				continue;
			}

			if (isStartOfNumberLiteral()) {
				String val = eatFloat();
				TokenType type = getNumberType(val);
				if (type.equals(INVALID)) {
					throw new LexerException("Invalid number literal " + getInfo());
				}
				Token token = createToken(type, val);
				emit(token);
				continue;
			}

			if (isStartOfStringLiteral()) {
				String val = eatStringLiteral();
				if (val == null) {
					throw new LexerException("Invalid number literal " + getInfo());
				}
				Token token = new Token(LIT_STR, val);
				emit(token);
				continue;
			}

			Optional<Token> operator = tryParseOperator();
			if (operator.isPresent()) {
				Token token = operator.get();
				token.setStartChar(start);
				token.setRow(getRow());
				emit(token);
			} else {
				throw new LexerException("Invalid token '" + peek() + "' " + getInfo());
			}
		}
	}

	public void begin() {
		emit(createToken(_START, null));
		doLex();
		emit(createToken(_END, null));
	}

	private boolean isStartOfIdentifier() {
		return peek().matches("[a-zA-Z$]");
	}

	private boolean isStartOfNumberLiteral() {
		return peek().matches("[0-9-]");
	}

	private boolean isStartOfStringLiteral() {
		return peek().equals("'") || peek().equals("\"");
	}

	private Token createToken(TokenType type, String val) {
		Token token = new Token(type, val);
		token.setStartChar(start);
		token.setRow(getRow());
		return token;
	}

	private Optional<Token> tryParseOperator() {
		String curr = next();
		switch (curr) {
			case "!":
				if (peek().equals("=")) {
					curr += next();
					return Token.optional(NE, curr);
				} else {
					return Token.optional(NOT, curr);
				}
			case "=":
				if (peek().equals("=")) {
					curr += next();
					return Token.optional(EQ, curr);
				} else {
					return Token.optional(ASSN, curr);
				}
			case "+":
				if (peek().equals("+")) {
					curr += next();
					return Token.optional(INC, curr);
				} else {
					return Token.optional(ADD, curr);
				}
			case "-":
				if (peek().equals("-")) {
					curr += next();
					return Token.optional(DEC, curr);
				} else {
					return Token.optional(SUB, curr);
				}
			case "/":
				return Token.optional(DIV, curr);
			case "*":
				return Token.optional(MUL, curr);
			case "%":
				return Token.optional(MOD, curr);
			case "&":
				if (peek().equals("&")) {
					curr += next();
					return Token.optional(AND, curr);
				} else {
					return Token.empty();
				}
			case "|":
				if (peek().equals("|")) {
					curr += next();
					return Token.optional(OR, curr);
				} else {
					return Token.empty();
				}
			case ">":
				if (peek().equals("=")) {
					curr += next();
					return Token.optional(GE, curr);
				} else {
					return Token.optional(GT, curr);
				}
			case "<":
				if (peek().equals("=")) {
					curr += next();
					return Token.optional(LE, curr);
				} else {
					return Token.optional(LT, curr);
				}
			case ".":
				return Token.optional(DOT, curr);
			case ",":
				return Token.optional(COMMA, curr);
			case " ":
				return Token.optional(SPACE, curr);
			case "\t":
				return Token.optional(TAB, curr);
			case "\n":
				return Token.optional(LF, curr);
			case ";":
				return Token.optional(SCOL, curr);
			case "(":
				return Token.optional(LPAREN, curr);
			case ")":
				return Token.optional(RPAREN, curr);
			case "{":
				return Token.optional(LBRACE, curr);
			case "}":
				return Token.optional(RBRACE, curr);
			case "[":
				return Token.optional(LBRACK, curr);
			case "]":
				return Token.optional(RBRACK, curr);
			default:
				rewind();
				return Optional.empty();
		}
	}

	public TokenType classifyToken(String tokenString) {
		return values.getOrDefault(tokenString, IDEN);
	}

	public static boolean isStringLiteral(Token token) {
		return (token.getValue().startsWith("\"") && token.getValue().endsWith("\"")) ||
				(token.getValue().startsWith("'") && token.getValue().endsWith("'"));
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public static TokenType getNumberType(String val) {
		try {
			float parsed = Float.parseFloat(val);
			if (parsed == (int) parsed) {
				return LIT_INT;
			} else {
				return LIT_FLT;
			}
		} catch (NumberFormatException ex) {
			return INVALID;
		}
	}

}
