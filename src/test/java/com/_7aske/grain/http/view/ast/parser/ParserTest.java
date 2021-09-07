package com._7aske.grain.http.view.ast.parser;

import com._7aske.grain.http.view.ast.AstNode;
import com._7aske.grain.http.view.ast.lexer.Lexer;
import org.junit.jupiter.api.Test;

class ParserTest {

	@Test
	void test_parser() {
		String code = "1 if ('username' == null) { a = 1 } else { b = 3 }";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
		Parser parser = new Parser(lexer.getTokens());
		AstNode ast = parser.parse();

		System.out.println(ast);
		System.out.println();
		printAst(ast, 0);
	}

	void printAst(AstNode node, int depth) {
		if (node == null) return;
		printAst(node.getLeft(), depth + 1);
		for (int i = 0; i < depth; ++i)
			System.out.print("    ");
		System.out.println(node);
		System.out.println();
		printAst(node.getRight(), depth + 1);
	}
}