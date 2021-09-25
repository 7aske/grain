package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;
import org.junit.jupiter.api.Test;

class InterpreterTest {
	@Test
	void test_testInterpreter() {
		String code = "val =10 if val > 9 then username = 'test' if true then username='test2' endif endif";
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

}