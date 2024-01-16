package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.ast.basic.AstUnaryNode;
import com._7aske.grain.gtl.interpreter.Interpreter;

public class AstImportNode extends AstUnaryNode {
	public AstSymbolNode alias;
	public void setPackage(AstNode node) {
		setNode(node);
	}

	public AstNode getPackage() {
		return super.getNode();
	}

	public AstSymbolNode getAlias() {
		return alias;
	}

	public void setAlias(AstSymbolNode alias) {
		this.alias = alias;
	}

	@Override
	public Object run(Interpreter interpreter) {
		Object value;
		String packageName = (String) getNode().run(interpreter);
		String[] parts = packageName.split("\\.");
		String className = parts[parts.length - 1];
		value = loadClass(packageName);
		if (alias == null) {
			interpreter.putSymbol(className, value);
		} else {
			interpreter.putSymbol(alias.getName(), value);
		}
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
