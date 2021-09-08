package com._7aske.grain.http.view.ast.parser;

import com._7aske.grain.http.view.ast.AstBinaryNode;
import com._7aske.grain.http.view.ast.AstNode;
import com._7aske.grain.http.view.ast.AstTernaryNode;
import com._7aske.grain.http.view.ast.AstUnaryNode;
import com._7aske.grain.http.view.ast.lexer.Lexer;
import org.junit.jupiter.api.Test;

class ParserTest {

	@Test
	void test_parser() {
		String code = "1 if ('username' == null) { a = 1 } else { b = 3 }";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();

		System.out.println(ast);
		System.out.println();
		printAst(ast, 0);
	}

	@Test
	void test_parseBlock() {
		String code = "if ('username' == null) { a = 1 }";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();

		printAst(ast, 0);
	}

	@Test
	void test_assign() {
		String code = "num = 2";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();

		printAst(ast, 0);
	}

	void printAst(AstNode node, int depth) {
		if (node == null) return;

		if (node instanceof AstBinaryNode) {
			printAst(((AstBinaryNode) node).getLeft(), depth + 1);
			for (int i = 0; i < depth + 1; ++i) System.out.print("    ");
			System.out.println("↑");
		} else if (node instanceof AstTernaryNode) {
			printAst(((AstTernaryNode) node).getLeft(), depth + 1);
			for (int i = 0; i < depth + 1; ++i) System.out.print("    ");
			System.out.println("↑");
		}

		for (int i = 0; i < depth; ++i) System.out.print("    ");
		System.out.print(node);
		if (node instanceof AstUnaryNode) {
			System.out.printf(" -> %s", ((AstUnaryNode) node).getNode());
		}
		System.out.println();

		if (node instanceof AstBinaryNode) {
			for (int i = 0; i < depth + 1; ++i) System.out.print("    ");
			System.out.println("↓");
			printAst(((AstBinaryNode) node).getRight(), depth + 1);
		} else if (node instanceof AstTernaryNode) {
			for (int i = 0; i < depth + 1; ++i) System.out.print("    ");
			System.out.println("↓");
			printAst(((AstTernaryNode) node).getRight(), depth + 1);
		}
	}
}