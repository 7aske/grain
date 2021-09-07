package com._7aske.grain.http.view.ast.lexer;

import org.junit.jupiter.api.Test;

class LexerTest {

	@Test
	void test_lexer() {
		String code = "if (username != null && true == false)\na = a + 1.2";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
	}


	@Test
	void test_lexer2() {
		String code = "username = 123";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
	}
}