package com._7aske.grain.compiler.util;

import com._7aske.grain.compiler.ast.*;
import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstTernaryNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;

import java.util.List;

public class AstUtil {
	private AstUtil() {
	}

	public static void getSymbols(List<AstNode> asts, List<String> symbolsName) {
		for (AstNode ast : asts) {
			getSymbols(ast, symbolsName);
		}
	}

	public static void getSymbols(AstNode node, List<String> symbolNames) {
		if (node == null) return;
		if (node instanceof AstSymbolNode) {
			symbolNames.add(((AstSymbolNode) node).getName());
		} else if (node instanceof AstObjectReferenceNode) {
			if (((AstObjectReferenceNode) node).getBackReference() == null) {
				symbolNames.add(((AstObjectReferenceNode) node).getName());
			}
		}

		if (node instanceof AstTernaryNode) {
			getSymbols(((AstTernaryNode) node).getLeft(), symbolNames);
		} else if (node instanceof AstBinaryNode) {
			getSymbols(((AstBinaryNode) node).getLeft(), symbolNames);
		} else if (node instanceof AstRootNode) {
			getSymbols(((AstRootNode) node).getNodes(), symbolNames);
		} else if (node instanceof AstUnaryNode) {
			getSymbols(((AstUnaryNode) node).getNode(), symbolNames);
		} else if (node instanceof AstBlockNode) {
			getSymbols(((AstBlockNode) node).getNodes(), symbolNames);
		}
	}
}
