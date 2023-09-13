package com._7aske.grain.compiler.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com._7aske.grain.compiler.lexer.TokenType.*;

class LexerTest {

	@Test
	void test_lexer() {
		String code = "if (username != null && true == false) then\na = a + 1.2";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
		List<Token> tokens = lexer.getTokens();
		Assertions.assertEquals(_START, tokens.get(0).getType());
		Assertions.assertEquals(IF, tokens.get(1).getType());
		Assertions.assertEquals(LPAREN, tokens.get(2).getType());
		Assertions.assertEquals(IDEN, tokens.get(3).getType());
		Assertions.assertEquals(NE, tokens.get(4).getType());
		Assertions.assertEquals(NULL, tokens.get(5).getType());
		Assertions.assertEquals(AND, tokens.get(6).getType());
		Assertions.assertEquals(TRUE, tokens.get(7).getType());
		Assertions.assertEquals(EQ, tokens.get(8).getType());
		Assertions.assertEquals(FALSE, tokens.get(9).getType());
		Assertions.assertEquals(RPAREN, tokens.get(10).getType());
		Assertions.assertEquals(THEN, tokens.get(11).getType());
		Assertions.assertEquals(IDEN, tokens.get(12).getType());
		Assertions.assertEquals(ASSN, tokens.get(13).getType());
		Assertions.assertEquals(IDEN, tokens.get(14).getType());
		Assertions.assertEquals(ADD, tokens.get(15).getType());
		Assertions.assertEquals(LIT_FLT, tokens.get(16).getType());
		Assertions.assertEquals(_END, tokens.get(tokens.size() - 1).getType());
	}


	@Test
	void test_lexer2() {
		String code = "username = 123";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
	}
}