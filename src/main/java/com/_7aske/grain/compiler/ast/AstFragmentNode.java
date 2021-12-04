package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.http.view.FileView;

import java.util.List;

public class AstFragmentNode extends AstNode {
	private FileView fileView;
	private AstSymbolNode symbol;
	private List<AstNode> arguments;

	public AstFragmentNode(FileView fileView) {
		this.fileView = fileView;
	}

	public FileView getFileView() {
		return fileView;
	}

	public void setFileView(FileView fileView) {
		this.fileView = fileView;
	}

	public AstSymbolNode getSymbol() {
		return symbol;
	}

	public void setSymbol(AstSymbolNode symbol) {
		this.symbol = symbol;
	}

	public List<AstNode> getArguments() {
		return arguments;
	}

	public void setArguments(List<AstNode> arguments) {
		this.arguments = arguments;
	}

	@Override
	public Object run(Interpreter interpreter) {
		// @Incomplete we need to parse arguments
		return fileView.getContent();
	}
}
