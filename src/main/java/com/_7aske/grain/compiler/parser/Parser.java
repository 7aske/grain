package com._7aske.grain.compiler.parser;

import com._7aske.grain.compiler.ast.*;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.Token;
import com._7aske.grain.compiler.lexer.TokenType;
import com._7aske.grain.compiler.parser.exception.ParserOperationNotSupportedException;
import com._7aske.grain.compiler.parser.exception.ParserSyntaxErrorException;
import com._7aske.grain.compiler.types.AstBooleanOperator;
import com._7aske.grain.compiler.types.AstEqualityOperator;
import com._7aske.grain.compiler.types.AstLiteralType;

import java.util.List;
import java.util.Stack;

import static com._7aske.grain.compiler.lexer.TokenType.*;

public class Parser {
	private final Lexer lexer;
	private final List<Token> tokens;
	private final TokenIterator iter;
	private Stack<AstNode> parentStack = new Stack<>();

	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.tokens = lexer.getTokens();
		this.iter = new TokenIterator(tokens);
	}

	public AstNode parse() {
		AstBlockNode blockNode = new AstBlockNode();
		if (!iter.isPeekOfType(_START))
			throw new ParserSyntaxErrorException("Token _START missing at the beginning of code block");
		iter.next();
		while (iter.hasNext() && !iter.isPeekOfType(_END)) {
			AstRootNode root = new AstRootNode();
			AstNode node = parseExpression(root);
			root.setNode(node);
			blockNode.addNode(root);
		}
		return blockNode;
	}

	private AstNode parseExpression(AstNode parent) {
		AstNode node = null;
		if (iter.isPeekOfType(IF, ELIF)) {
			node = parseIf();
		} else {
			node = parseSubExpression();
		}
		if (node != null)
			node.setParent(parent);

		return node;
	}

	private AstNode parseIf() {
		Token token = iter.next();
		if (token.isOfType(ELIF) && !(parentStack.peek() instanceof AstIfNode)) {
			printSourceCodeLocation(token);
			throw new ParserSyntaxErrorException("No matching '%s' token found for '%s'", IF.getValue(), token.getValue());
		}

		int indexOfThen = findNextOf(THEN);
		if (indexOfThen == -1) {
			printSourceCodeLocation(token);
			throw new ParserSyntaxErrorException("No matching '%s' token found for '%s'", THEN.getValue(), token.getValue());
		}

		int indexOfEndif = findNextOf(ENDIF);
		if (indexOfEndif == -1) {
			printSourceCodeLocation(token);
			throw new ParserSyntaxErrorException("No matching '%s' token found for '%s'", ENDIF.getValue(), token.getValue());
		}

		AstIfNode ifNode = (AstIfNode) createNode(token);
		parentStack.push(ifNode);
		AstNode condition = parseSubExpression(indexOfThen);
		iter.next(); // skip then
		ifNode.setCondition(condition);
		AstBlockNode trueBlock = new AstBlockNode();
		parentStack.push(trueBlock);
		while (iter.hasNext() && !iter.isPeekOfType(ELSE, ELIF, ENDIF)) {
			trueBlock.addNode(parseSubExpression());
		}
		parentStack.pop();
		ifNode.setIfTrue(trueBlock);

		// ELSE OR ELIF
		int indexOfElif = findNextOf(ELIF);
		int indexOfElse = findNextOf(ELSE);
		AstNode elseBlock = null;
		if (indexOfElif != -1) {
			elseBlock = parseIf();
		} else if (indexOfElse != -1) {
			elseBlock = new AstBlockNode();
			parentStack.push(elseBlock);
			iter.next(); // skip else
			while (iter.hasNext() && !iter.isPeekOfType(ELSE, ELIF, ENDIF)) {
				((AstBlockNode) elseBlock).addNode(parseSubExpression(indexOfEndif));
			}
		}
		ifNode.setIfFalse(elseBlock);
		parentStack.pop();
		iter.next(); // skip endif

		return ifNode;
	}

	private AstNode parseSubExpression() {
		return parseSubExpression(Integer.MAX_VALUE);
	}

	private AstNode parseSubExpression(int end) {
		int start = iter.getIndex();

		if (start >= end)
			return null;
		if (iter.isPeekOfType(_END))
			return null;

		Token curr = iter.next();
		if (curr.isOfType(_START))
			curr = iter.next();

		AstNode node = null;


		if (iter.isPeekOfType(AND, OR)) {
			node = createNode(curr);
			Token token = iter.next();
			AstBooleanNode booleanNode = (AstBooleanNode) createNode(token);
			parentStack.push(booleanNode);
			booleanNode.setLeft(node);
			booleanNode.setOperator(AstBooleanOperator.from(token.getType()));
			booleanNode.setRight(parseSubExpression(end));
			parentStack.pop();
			node = booleanNode;
		} else if (iter.isPeekOfType(EQ)) {
			node = createNode(curr);
			Token token = iter.next();
			AstEqualityNode equalityNode = (AstEqualityNode) createNode(token);
			parentStack.push(equalityNode);
			equalityNode.setLeft(node);
			equalityNode.setOperator(AstEqualityOperator.from(token.getType()));
			equalityNode.setRight(parseSubExpression(end));
			if (equalityNode.getRight() instanceof AstBooleanNode) {
				AstBooleanNode booleanNode = (AstBooleanNode) equalityNode.getRight();
				AstNode booleanLeft = booleanNode.getLeft();
				booleanNode.setLeft(equalityNode);
				equalityNode.setRight(booleanLeft);
				node = booleanNode;
			} else {
				node = equalityNode;
			}
			parentStack.pop();
		} else {
			node = createNode(curr);
		}

		return node;
	}

	private AstNode createNode(Token token) {
		AstNode astNode = doCreateNode(token);
		astNode.setParent(parentStack.size() == 0 ? null : parentStack.peek());
		return astNode;
	}

	private AstNode doCreateNode(Token token) {
		switch (token.getType()) {
			case IDEN:
				return new AstSymbolNode(token.getValue());
			case LIT_STR:
			case LIT_INT:
			case LIT_FLT:
				return new AstLiteralNode(AstLiteralType.from(token.getType()), token.getValue());
			case ELIF:
			case IF:
				return new AstIfNode();
			case ELSE:
				break;
			case FOR:
				break;
			case IN:
				break;
			case CONTINUE:
				break;
			case BREAK:
				break;
			case TRUE:
			case FALSE:
				return new AstLiteralNode(AstLiteralType.BOOLEAN, token.getValue());
			case NULL:
				return new AstLiteralNode(AstLiteralType.NULL, token.getValue());
			case ASSN:
				return new AstAssignmentNode();
			case ADD:
				break;
			case SUB:
				break;
			case DIV:
				break;
			case MUL:
				break;
			case MOD:
				break;
			case AND:
			case OR:
				return new AstBooleanNode(AstBooleanOperator.from(token.getType()));
			case NOT:
				break;
			case EQ:
				return new AstEqualityNode(AstEqualityOperator.from(token.getType()));
			case NE:
				break;
			case GT:
				break;
			case LT:
				break;
			case GE:
				break;
			case LE:
				break;
			case INC:
				break;
			case DEC:
				break;
			case DOT:
				break;
			case COMMA:
				break;
			case SPACE:
				break;
			case TAB:
				break;
			case LF:
				break;
			case SCOL:
				return new AstExpressionEndNode();
			case LPAREN:
				break;
			case RPAREN:
				break;
			case LBRACE:
				return new AstBlockNode();
			case RBRACE:
				break;
			case LBRACK:
				break;
			case RBRACK:
				break;
			case INVALID:
				break;
		}
		throw new ParserOperationNotSupportedException("Token " + token.getType() + " not supported.");
	}

	private void printSourceCodeLocation(Token token) {
		String content = lexer.getContent();
		int index = 0;
		for (int i = 0; i < token.getRow(); i++) {
			index = content.indexOf("\n", index);
		}
		if (index == -1) {
			index = 0;
		}
		int lastIndex = content.indexOf("\n", index);
		if (lastIndex == -1) {
			lastIndex = content.length();
		}
		String line = content.substring(index, lastIndex);
		System.err.println(line);
		for (int i = 0; i < token.getStartChar() - 2; i++) {
			System.err.print(" ");
		}
		for (int i = 0; i < token.getValue().length(); i++) {
			System.err.print("^");
		}
		System.err.println();
		for (int i = 0; i < token.getStartChar() - 2; i++) {
			System.err.print("─");
		}
		System.err.print("┘");

		System.err.println();
	}

	private int findNextOf(TokenType... types) {
		int relativeIndex = 0;
		while (iter.hasNext()) {
			Token next = iter.next();
			relativeIndex++;

			if (next.isOfType(types))  {
				iter.rewind(relativeIndex);
				return iter.getIndex() + relativeIndex;
			}
		}
		iter.rewind(relativeIndex);
		return -1;
	}
}
