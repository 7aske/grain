package com._7aske.grain.compiler.interpreter;

import com._7aske.grain.compiler.ast.AstFunctionCallback;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.parser.Parser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
	private static Map<String, Object> debugSymbols = new HashMap<>();

	static {
		debugSymbols.put("dbg", (AstFunctionCallback) (args) -> {
			System.err.println(Arrays.toString(args));
			return args;
		});
	}

	@Test
	void test_testInterpreter() {
		String code = "val = 10; if (val > 9) { username = 'test'; if (true) { username='test2';}}";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("username", "test");
		interpreter.run();
		System.out.println(interpreter.getSymbols());
	}

	@Test
	void test_testInterpreterReturn() {
		String code = "10 + 10";
		Interpreter interpreter = new Interpreter(code, null);
		Object retval = interpreter.run();
		assertEquals(20, retval);
	}

	@Test
	void test_dbgln() {
		String code = "a = dbgln(dbgln('45'))";
		Lexer lexer = new Lexer(code);
		Parser parser = new Parser(lexer);
		AstFunctionCallback dbgln = (args) -> {
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
		AstFunctionCallback now = (args) -> {
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
	void test_boolean() {
		String code = "a = false; b = !(a || true) && true";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("b");
		assertEquals(false, val);
	}

	@Test
	void test_booleanPrecedence() {
		String code = "a = 1; b = 2; c = a == 1 && b == 2";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("c");
		assertEquals(true, val);
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
	void test_plusWithFloatDotZeroAndInteger() {
		String code = "a = 1.0 + 2";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals(3.0, (float) val, 0.001);
	}

	@Test
	void test_plusWithFloatAndInteger() {
		String code = "a = 1.1 + 2";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals(3.1, (float) val, 0.001);
	}

	@Test
	void test_plusOperationWithParenthesis() {
		String code = "a = (1 + 2) * 10";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals(30, val);
	}

	@Test
	void test_divWithZero() {
		String code = "a = 1 / 0";
		Interpreter interpreter = new Interpreter(code, null);
		assertThrows(ArithmeticException.class, interpreter::run);
	}

	@Test
	void test_plusStrings() {
		String code = "str = 'abc'; a = str + 'b'";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals("abcb", val);
	}

	@Test
	void test_repeatString() {
		String code = "str = 'abc'; a = str * 10";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		Object val = interpreter.getSymbolValue("a");
		assertEquals("abcabcabcabcabcabcabcabcabcabc", val);
	}

	@Test
	void test_forLoop() {
		String code = "for (a = 0; a < 10; a = a + 1) { print(a); }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		String content = interpreter.getContent();
		assertEquals("0123456789", content);
	}

	@Test
	void test_forLoop_noIncrement() {
		String code = "for (a = 0; a < 10;) { print(a); a = a + 1; }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		String content = interpreter.getContent();
		assertEquals("0123456789", content);
	}

	@Test
	@Disabled
	void test_forLoop_spiderLoop() {
		String code = "for (;;) { print('a');}";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		String content = interpreter.getContent();
		assertEquals("0123456789", content);
	}

	@Test
	void test_scope() {
		String code = "{ a = 0; }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		Object content = interpreter.getSymbolValue("a");
		assertNull(content);
	}

	@Test
	void test_scopedVariable() {
		String code = " a = 0; { a = 1; b = 0 }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		Object a = interpreter.getSymbolValue("a");
		Object b = interpreter.getSymbolValue("b");
		assertEquals(1, a);
		assertNull(b);
	}

	@Test
	void test_objectReferenceMethod() {
		String code = "a = date.getMonthValue();";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.putSymbol("date", LocalDate.parse("2020-10-10"));
		interpreter.run();
		Object a = interpreter.getSymbolValue("a");
		assertEquals(10, a);
	}

	@Test
	void test_objectReferenceAsClass() {
		String code = "a = Integer.parseInt('12');";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.putSymbol("Integer", Integer.class);
		interpreter.run();
		Object a = interpreter.getSymbolValue("a");
		assertEquals(12, a);
	}

	@Test
	void test_arrayIndex() {
		String code = "b = a[0];";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		List<String> list = List.of("a", "b", "c");
		interpreter.putSymbol("a", list);
		interpreter.run();
		Object b = interpreter.getSymbolValue("b");
		assertEquals("a", b);
	}

	@Test
	void test_arrayIndexAssignment() {
		String code = "a[0] = 'g';";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("aaa");
		list.add("c");
		interpreter.putSymbol("a", list);
		interpreter.run();
		List<Object> newList = (List<Object>) interpreter.getSymbolValue("a");
		assertEquals("g", (newList.get(0)));
	}

	@Test
	void test_arrayIndexCall() {
		String code = "a[0] = print; b = a[0]; b('test');";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		List<String> list = new ArrayList<>();
		list.add("elem");
		interpreter.putSymbol("a", list);
		interpreter.run();
		assertEquals("test", interpreter.getContent());
	}

	@Test
	void test_arrayAdd() {
		String code = "a.add('test');";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		List<String> list = new ArrayList<>();
		list.add("elem");
		interpreter.putSymbol("a", list);
		interpreter.run();
		List<Object> updatedList = (List<Object>) interpreter.getSymbolValue("a");
		assertEquals("test", updatedList.get(1));
	}

	@Test
	void test_integerParseInt() {
		String code = "a = Integer.parseInt(('10')) + 20;";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.putSymbol("Integer", Integer.class);
		interpreter.run();
		assertEquals(30, interpreter.getSymbolValue("a"));
	}

	@Test
	void test_import() {
		String code = "import 'java.time.LocalDate';" +
				"a = LocalDate.parse('2020-10-10');" +
				"a = a.toString();";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		assertEquals("2020-10-10", interpreter.getSymbolValue("a"));
	}

	@Test
	void test_chainedFunctionCalls() {
		String code = "import 'java.time.LocalDate';" +
				"a = LocalDate.parse('2020-10-10').toString() + '-10';";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		assertEquals("2020-10-10-10", interpreter.getSymbolValue("a"));
	}

	static class Category {
		private Long id;
		private String name;
	}

	static class Post {
		private Long id;
		private String title;
		private Category category;
	}

	@Test
	void test_chainedObjectReference() {
		Post post = new Post();
		Category category = new Category();
		category.name = "Test";
		post.category = category;
		String code = "a = post.category.name";

		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.putSymbol("post", post);
		interpreter.run();

		assertEquals("Test", interpreter.getSymbolValue("a"));
	}

	@Test
	void test_chainedObjectReferencePrecedence() {
		Post post = new Post();
		Category category = new Category();
		category.name = "Test";
		post.category = category;
		String code = "a = post.category.name + 'Toma' + 'Test'";

		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.putSymbol("post", post);
		interpreter.run();

		assertEquals("TestTomaTest", interpreter.getSymbolValue("a"));
	}

	@Test
	void test_callExpression() {
		String code = "a = String('b') + 'a';";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		assertEquals("ba", interpreter.getSymbolValue("a"));
	}

	@Test
	void test_functionCallInForCondition() {
		String code = "for (i = 0; i < String('aaa').length(); i = i + 1) { print('a'); }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		assertEquals("aaa", interpreter.getContent());
	}

	@Test
	void test_forLoopBreak() {
		String code = "for (i = 0; i < 10; i = i + 1) { if (i == 5) {break;} else { 1; } dbg(i, i); print('a'); }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		assertEquals("aaaaa", interpreter.getContent());
	}

	@Test
	void test_forLoopContinue() {
		String code = "for (i = 0; i < 10; i = i + 1) { if (i % 2 == 0) { continue; } print(i); }";
		Interpreter interpreter = new Interpreter(code, debugSymbols);
		interpreter.run();
		assertEquals("13579", interpreter.getContent());
	}

	@Test
	void test_forLoopDecrement() {
		String code = "for (a = 10; a > 0; a = a - 1) { print('a'); }";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("aaaaaaaaaa", interpreter.getContent());
	}

	@Test
	void test_unaryMinusPlus() {
		String code = "a = -1 + 1; b = 1 + -1; c = 1 + -(1+1); d = 1-+1; e = +1;";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals(0, interpreter.getSymbolValue("a"));
		assertEquals(0, interpreter.getSymbolValue("b"));
		assertEquals(-1, interpreter.getSymbolValue("c"));
		assertEquals(0, interpreter.getSymbolValue("d"));
		assertEquals(1, interpreter.getSymbolValue("e"));
	}

	static class User {
		private String username;

		public User(String username) {
			this.username = username;
		}
	}

	@Test
	void test_objectReference() {
		String code = "print(user.username);";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("user", new User("test"));
		interpreter.run();
		assertEquals("test", interpreter.getContent());
	}

	@Test
	void test_postIncrementDecrement() {
		String code = "i = 0; i++; i--;";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals(0, interpreter.getSymbolValue("i"));
	}

	@Test
	void test_forEach() {
		List<String> list = List.of("Test1", "Test2", "Test3", "Test4");
		String code = "foreach item in list { if (item == 'Test2') {continue;} print(item); }";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("list", list);
		interpreter.run();
		assertEquals("Test1Test3Test4", interpreter.getContent());
	}

	@Test
	void test_forEachString() {
		String code = "foreach char in 'list of characters' { print(char); }";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("list of characters", interpreter.getContent());
	}

	@Test
	void test_defaultExpression() {
		String code = "a = b ?? 'hello'; c = d ?? 'test';";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("d", "world");
		interpreter.run();
		assertEquals("hello", interpreter.getSymbolValue("a"));
		assertEquals("world", interpreter.getSymbolValue("c"));
	}

	@Test
	void test_defaultExpressionMethodCall() {
		String code = "a = nullable() ?? ('hello' + 'world'); c = (callable() ?? 'test') + 'hello';";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("nullable", (AstFunctionCallback) (args) -> null);
		interpreter.putSymbol("callable", (AstFunctionCallback) (args) -> "world");
		interpreter.run();
		assertEquals("helloworld", interpreter.getSymbolValue("a"));
		assertEquals("worldhello", interpreter.getSymbolValue("c"));
	}

	@Test
	void test_ternaryOperator() {
		String code = "a = (test == true) ? 1 : 2";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("test", true);
		interpreter.run();
		assertEquals(1, interpreter.getSymbolValue("a"));
	}

	@Test
	void test_ternaryOperatorMethodCalls() {
		String code = "a = callable() ? nullable() : 2";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.putSymbol("test", true);
		interpreter.putSymbol("nullable", (AstFunctionCallback) (args) -> null);
		interpreter.putSymbol("callable", (AstFunctionCallback) (args) -> "world");
		interpreter.run();
		assertNull(interpreter.getSymbolValue("a"));
	}

	@Test
	void test_range() {
		String code = "foreach num in range(10) { print(num); }";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("0123456789", interpreter.getContent());
	}


	@Test
	void test_include() {
		String code = "include 'card.html' as card;";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
	}

	@Test
	void test_fragment() {
		String code = "include 'card.html' as card; #card();";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("<div>this is a card</div>", interpreter.getContent());
	}

	@Test
	void test_keywordArguments() {
		String code = "include 'parametrized-card.html' as card; #card(title='Title', body='Body');";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("<div>\t<h2>Title</h2>\t<p>Body</p></div>", interpreter.getContent());
	}

	@Test
	void test_oneLineForEach() {
		String code = "foreach num in range(10) print(num);";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("0123456789", interpreter.getContent());
	}

	@Test
	void test_oneLineIf() {
		String code = "if (false) print('test') else print('test2'); print('test1');";
		Interpreter interpreter = new Interpreter(code, null);
		interpreter.run();
		assertEquals("test2test1", interpreter.getContent());
	}
}