package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstImportNode extends AstUnaryNode {
	public void setPackage(AstNode node) {
		setNode(node);
	}

	public AstNode getPackage() {
		return super.getNode();
	}

	@Override
	public Object run(Interpreter interpreter) {
		Object value;
		String packageName = (String) getNode().run(interpreter);
		String[] parts = packageName.split("\\.");
		String className = parts[parts.length - 1];
		value = loadClass(packageName);
		interpreter.putSymbol(className, value);
		return value;
	}

	private Class<?> loadClass(String classPath) {
		try {
			return getClass().getClassLoader().loadClass(classPath);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
