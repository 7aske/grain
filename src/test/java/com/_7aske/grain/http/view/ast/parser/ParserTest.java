package com._7aske.grain.http.view.ast.parser;

import com._7aske.grain.http.view.ast.*;
import com._7aske.grain.http.view.ast.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;

class ParserTest {

	@Test
	void test_parser() {
		String code = "1 if ('username' == null) { a = 1 } else { b = 3 }";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
		Parser parser = new Parser(lexer);
		List<AstNode> asts = parser.parse();

		printAst(asts, 0);
	}

	@Test
	void test_parseBlock() {
		String code = "{username = 'test' password = 'test';} {test = 1}";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		List<AstNode> asts = parser.parse();

		printAst(asts, 0);
	}

	@Test
	void test_assign() {
		String code = "num = 2";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		List<AstNode> asts = parser.parse();

		printAst(asts, 0);
	}


	@Test
	void test_if() {
		String code = "if (test == true) { test == false } else if (test == false) { printf } else { testf }";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		List<AstNode> asts = parser.parse();

		printAst(asts, 0);
	}

	@Test
	void test_while() {
		String code = "while (true) { test = 1 }";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		List<AstNode> asts = parser.parse();

		printAst(asts, 0);
	}

	void printAst(List<AstNode> asts, int depth) {
		for (AstNode ast : asts) {
			for (int i = 0; i < depth; ++i) System.out.print("    ");
			System.out.println("{ BLOCK START");
			printAst(ast, depth);
			for (int i = 0; i < depth; ++i) System.out.print("    ");
			System.out.println("} BLOCK END");
		}
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
		if (node instanceof AstBlockNode) {
			System.out.println(node);
			printAst(((AstBlockNode) node).getNodes(), depth + 1);
		} else {
			System.out.print(node);
			if (node instanceof AstUnaryNode) {
				System.out.printf(" -> %s", ((AstUnaryNode) node).getNode());
			}
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