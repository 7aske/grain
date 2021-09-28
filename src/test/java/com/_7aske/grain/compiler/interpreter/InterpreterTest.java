package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstArithmeticNode;
import com._7aske.grain.compiler.ast.AstFunctionCallNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InterpreterTest {
	@Test
	void test_testInterpreter() {
		String code = "val = 10; if (val > 9) { username = 'test'; if (true) { username='test2';}}";
		Lexer lexer = new Lexer(code);
		lexer.begin();
		Parser parser = new Parser(lexer);
		AstNode ast = parser.parse();
		Interpreter interpreter = new Interpreter();
		interpreter.addNode(ast);
		interpreter.putSymbol("username", "test");
		interpreter.run();
		System.out.println(interpreter.getSymbols());
	}

	@Test
	void test_dbgln() {
		String code = "a = dbgln(dbgln('45'))";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstFunctionCallNode.AstFunctionCallback dbgln = (args) -> {
			System.out.println(args[0]);
			return args[0];
		};
		Interpreter interpreter = new Interpreter(parser);
		interpreter.putSymbol("dbgln", dbgln);
		interpreter.run();
	}

	@Test
	void test_now() {
		String code = "a = date; a = a('2020-10-10')";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstFunctionCallNode.AstFunctionCallback now = (args) -> {
			if (args.length == 1) {
				return LocalDate.parse((String) args[0]).toString();
			} else {
				return LocalDate.now().toString();
			}
		};

		Interpreter interpreter = new Interpreter(parser);
		interpreter.putSymbol("date", now);
		interpreter.run();
		System.out.println(interpreter.getSymbolValue("a"));
		assertNotNull(interpreter.getSymbolValue("a"));
	}


	@Test
	void test_callStaticMethod() {
		String code = "a = com._7aske.grain.util.NumberUtil.getNumberOrFloat('10')";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		Interpreter interpreter = new Interpreter(parser);
		interpreter.run();
		System.out.println(interpreter.getSymbolValue("a"));
		assertNotNull(interpreter.getSymbolValue("a"));
	}

	@Test
	void test_plusOperation() {
		String code = "a = 1 + 2";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals(3, val);
	}

	@Test
	void test_plusOperationWithParenthesis() {
		String code = "a = (1 + 2) * 10";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals(30, val);
	}
}