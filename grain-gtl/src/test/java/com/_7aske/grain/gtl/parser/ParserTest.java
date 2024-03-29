package com._7aske.grain.gtl.parser;

import com._7aske.grain.gtl.ast.*;
import com._7aske.grain.gtl.ast.basic.AstBinaryNode;
import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.ast.basic.AstTernaryNode;
import com._7aske.grain.gtl.ast.basic.AstUnaryNode;
import com._7aske.grain.gtl.interpreter.Interpreter;
import com._7aske.grain.gtl.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

	@Test
	void test_parser() {
		String code = "if ('username' == null) { a = 1 } else if (true) { b = 3 }";
		Lexer lexer = new Lexer(code);
		lexer.onEmit(System.out::println);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();

		printAst(ast, 0);
	}

	@Test
	void test_parseBlock() {
		String code = "{username = 'test'; password = 'test';} {test = 1;}";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}

	@Test
	void test_assign() {
		String code = "num = 2";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}

	@Test
	void test_bool() {
		String code = "a == 1 && b == 2 || c == 3";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}

	@Test
	void test_plusOperation(){
		String code = "(1 + 2) * 10";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}

	@Test
	void test_if() {
		String code = "if (!(username == null) && test == null) { test3; } else { print3; }";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}


	@Test
	void test_multipleIf() {
		String code = "if (username == null)     { r691634304='<a href=\"\">Log in</a>'; } else {}" +
				"if (username == \"test\") { r103406026='<h1>Hello <%=username%></h1>'; } else {}";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}

	@Test
	void test_elseIf() {
		String code = "if (username == null)     { r691634304='<a href=\"\">Log in</a>'; } else if (test == false) {}" +
				"if (username == \"test\") { r103406026='<h1>Hello <%=username%></h1>'; } else if (test == true) {}";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		printAst(ast, 0);
	}

	@Test
	void test_functionCall() {
		String code = "a = print('login')";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		Interpreter interpreter = new Interpreter();
		AstFunctionCallback print = (args) -> args[0];
		interpreter.putSymbol("print", print);
		interpreter.addNode(ast);
		interpreter.run();
		assertEquals("login", interpreter.getSymbolValue("a"));
	}

	@Test
	void test_objectReference() {
		String code = "context.request.headers();";
		Parser parser = new Parser(new Lexer(code));
		AstNode astNode = parser.parse();
		printAst(astNode, 0);
	}

	@Test
	void test_import() {
		String code = "import 'java.util.LocalDate';";
		Parser parser = new Parser(new Lexer(code));
		AstNode astNode = parser.parse();
		printAst(astNode, 0);
		assertTrue(((AstRootNode) astNode).getNodes().get(0) instanceof AstImportNode);
		assertTrue(((AstImportNode) ((AstRootNode) astNode).getNodes().get(0)).getNode() instanceof AstLiteralNode);
	}

	@Test
	void test_break(){
		String code = "if (true) { break; }";
		Parser parser = new Parser(new Lexer(code));
		AstNode astNode = parser.parse();
		printAst(astNode, 0);
	}

	@Test
	void test_unaryMinus() {
		String code = "a = -1 + 1; b = 1 + -1; c = 1 + -(1+1)";
		Parser parser = new Parser(new Lexer(code));
		AstNode program = parser.parse();
		printAst(program, 0);
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
			if (node instanceof AstIfNode) {
				System.out.println();
				printAst(((AstIfNode) node).getCondition(), depth);
			} else if (node instanceof AstRootNode) {
				System.out.println();
				printAst(((AstRootNode) node).getNode(), depth + 1);
			} else if (node instanceof AstUnaryNode) {
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