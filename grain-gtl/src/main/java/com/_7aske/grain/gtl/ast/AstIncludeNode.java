package com._7aske.grain.gtl.ast;

import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.interpreter.Interpreter;
import com._7aske.grain.gtl.interpreter.exception.InterpreterIncludeFailedException;
import com._7aske.grain.gtl.parser.Parser;
import com._7aske.grain.util.formatter.StringFormat;

import java.util.Optional;

public class AstIncludeNode extends AstNode {
	private String path;
	private AstSymbolNode identifier;
	// include "test" as fragment;
	// #fragment(param1, param2, ...);


	public AstIncludeNode() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public AstSymbolNode getIdentifier() {
		return identifier;
	}

	public void setIdentifier(AstSymbolNode identifier) {
		this.identifier = identifier;
	}

	@Override
	public Object run(Interpreter interpreter) {
		Optional<AstFragmentNode> fragmentNode = interpreter.tryIncludeFragment(path);
		if (fragmentNode.isEmpty()) {
			throw new InterpreterIncludeFailedException(StringFormat.format("Unable to include template '{}'", path));
		}
		interpreter.putSymbol(Parser.FRAGMENT_START + identifier.getName(), fragmentNode.get());
		return null; // Should we return null?
	}
}
