package com._7aske.grain.gtl.ast;

@FunctionalInterface
public interface AstFunctionCallback {
	Object call(Object... args);
}
