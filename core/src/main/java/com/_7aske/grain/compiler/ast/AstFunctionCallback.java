package com._7aske.grain.compiler.ast;

@FunctionalInterface
public interface AstFunctionCallback {
	Object call(Object... args);
}
