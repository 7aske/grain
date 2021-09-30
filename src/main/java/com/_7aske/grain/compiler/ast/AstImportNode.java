package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

public class AstImportNode extends AstUnaryNode {
	private Class<?> value;
	public void setPackage(AstNode node) {
		setNode(node);
	}

	public AstNode getPackage() {
		return super.getNode();
	}

	@Override
	public void run(Interpreter interpreter) {
		String packageName = (String) getNode().value();
		String[] parts  = packageName.split("\\.");
		String className = parts[parts.length - 1];
		this.value = loadClass(packageName);
		interpreter.putSymbol(className, this.value);
	}

	private Class<?> loadClass(String classPath) {
		try {
			return getClass().getClassLoader().loadClass(classPath);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object value() {
		return value;
	}
}
