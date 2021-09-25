package com._7aske.grain.compiler.parser;

import com._7aske.grain.compiler.ast.*;
import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.Token;
import com._7aske.grain.compiler.lexer.TokenType;
import com._7aske.grain.compiler.parser.exception.ParserOperationNotSupportedException;
import com._7aske.grain.compiler.parser.exception.ParserSyntaxErrorException;
import com._7aske.grain.compiler.types.AstBooleanOperator;
import com._7aske.grain.compiler.types.AstEqualityOperator;
import com._7aske.grain.compiler.types.AstLiteralType;
import com._7aske.grain.compiler.types.AstRelationalOperator;

import java.util.Stack;

import static com._7aske.grain.compiler.lexer.TokenType.*;

public class Parser {
	private final Lexer lexer;
	private TokenIterator iter;
	private final Stack<AstNode> parentStack = new Stack<>();
	private final Stack<AstNode> parsedStack = new Stack<>();

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public AstNode parse() {
		if (!lexer.isDone())
			lexer.begin();
		this.iter = new TokenIterator(lexer.getTokens());
		this.parentStack.clear();

		if (!iter.isPeekOfType(_START))
			throw new ParserSyntaxErrorException("Token _START missing at the beginning of code block");
		iter.next();

		AstBlockNode program = new AstBlockNode();
		parentStack.push(program);
		while (iter.hasNext() && !iter.isPeekOfType(_END)) {
			AstNode node = parseStatement();
			if (node != null)
				program.addNode(node);
		}
		return program;
	}

	private AstNode parseExpression() {
		AstNode node = null;
		node = parseSubExpression();
		return node;
	}

	private AstNode parseSubExpression() {
		Token curr = iter.next();
		AstNode node = null;

		if (curr.isOfType(LPAREN)) {
			node = parseSubExpression();
			return node;
		}


		if (iter.isPeekOfType(AND, OR)) {
			node = createNode(curr);
			Token token = iter.next();
			AstBooleanNode booleanNode = (AstBooleanNode) createNode(token);
			booleanNode.setLeft(node);
			booleanNode.setOperator(AstBooleanOperator.from(token.getType()));
			booleanNode.setRight(parseSubExpression());
			node = booleanNode;
		} else if (iter.isPeekOfType(EQ, NE)) {
			node = createNode(curr);
			Token token = iter.next();
			AstEqualityNode equalityNode = (AstEqualityNode) createNode(token);
			equalityNode.setLeft(node);
			equalityNode.setOperator(AstEqualityOperator.from(token.getType()));
			equalityNode.setRight(parseSubExpression());
			if (equalityNode.getRight().getPrecedence() > equalityNode.getPrecedence()) {
				AstBinaryNode booleanNode = (AstBinaryNode) equalityNode.getRight();
				AstNode booleanLeft = booleanNode.getLeft();
				booleanNode.setLeft(equalityNode);
				equalityNode.setRight(booleanLeft);
				node = booleanNode;
			} else {
				node = equalityNode;
			}
		} else if (iter.isPeekOfType(GT, LT, GE, LE)) {
			node = createNode(curr);
			Token token = iter.next();
			AstRelationalNode relationalNode = (AstRelationalNode) createNode(token);
			relationalNode.setLeft(node);
			relationalNode.setOperator(AstRelationalOperator.from(token.getType()));
			relationalNode.setRight(parseSubExpression());
			if (relationalNode.getRight().getPrecedence() > relationalNode.getPrecedence()) {
				AstBinaryNode booleanNode = (AstBinaryNode) relationalNode.getRight();
				AstNode booleanLeft = booleanNode.getLeft();
				booleanNode.setLeft(relationalNode);
				relationalNode.setRight(booleanLeft);
				node = booleanNode;
			} else {
				node = relationalNode;
			}
		} else if (iter.isPeekOfType(ASSN)) {
			if (!curr.isOfType(IDEN))
				throw new ParserSyntaxErrorException(getSourceCodeLocation(curr),
						"Cannot assign to '%s'", curr.getType());

			AstAssignmentNode astAssignmentNode = (AstAssignmentNode) createNode(iter.peek());
			AstSymbolNode symbolNode = (AstSymbolNode) createNode(curr);
			astAssignmentNode.setSymbol(symbolNode);
			iter.next(); // skip assn
			AstNode value = parseSubExpression();
			astAssignmentNode.setValue(value);
			node = astAssignmentNode;
		} else if (iter.isPeekOfType(SCOL)) {
			node = createNode(curr);
			iter.next();
		} else {
			if (curr.isOfType(NOT)) {
				AstNotNode astNotNode = (AstNotNode) createNode(curr);
				AstNode parsed = parseExpression();
				astNotNode.setNode(parsed);
				node = astNotNode;
			} else {
				node = createNode(curr);
				System.err.println(curr);
			}
		}

		if (parsedStack.empty() || parsedStack.peek() != node)
			parsedStack.push(node);
		if (iter.isPeekOfType(RPAREN)) {
			node = parseSubExpression();
		}

		return node;
	}

	private AstNode parseStatement() {
		AstNode node = null;
		if (iter.isPeekOfType(LBRACE)) {
			node = parseBlockStatement();
		} else if (iter.isPeekOfType(IF)) {
			node = parseIfStatement();
		} else if (iter.isPeekOfType(FOR)) {
			node = parseForStatement();
			// } else if (iter.isPeekOfType(ELSE)) {
			// 	throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Unexpected token '%s'", ELSE.getValue());
		} else {
			node = parseExpression();
		}
		return node;
	}

	private AstNode parseForStatement() {
		iter.next();
		return null;
	}

	private AstNode parseIfStatement() {
		Token ifToken = iter.next();
		if (!iter.isPeekOfType(LPAREN)) {
			throw new ParserSyntaxErrorException(getSourceCodeLocation(ifToken), "Expected '%s'", LPAREN.getValue());
		}
		AstIfNode ifNode = (AstIfNode) createNode(ifToken);
		AstNode condition = parseExpression();
		ifNode.setCondition(condition);
		AstNode ifTrueNode = parseStatement();
		ifNode.setIfTrue(ifTrueNode);
		if (iter.isPeekOfType(ELSE)) {
			iter.next();
			AstNode ifFalseNode = parseStatement();
			ifNode.setIfFalse(ifFalseNode);
		}

		return ifNode;
	}

	private AstNode parseBlockStatement() {
		AstBlockNode blockNode = (AstBlockNode) createNode(iter.next());

		while (!iter.isPeekOfType(RBRACE)) {
			AstNode node = parseStatement();
			if (node != null)
				blockNode.addNode(node);
		}
		iter.next(); // skip RBRACE

		return blockNode;
	}

	private AstNode createNode(Token token) {
		if (token.isOfType(RPAREN)) {
			return parsedStack.pop();
		}
		return doCreateNode(token);
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
				return new AstNotNode();
			case EQ:
			case NE:
				return new AstEqualityNode(AstEqualityOperator.from(token.getType()));
			case GT:
			case LT:
			case GE:
			case LE:
				return new AstRelationalNode(AstRelationalOperator.from(token.getType()));
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

	private String getSourceCodeLocation(Token token) {
		StringBuilder out = new StringBuilder();
		String content = lexer.getContent();
		int index = 0;
		for (int i = 0; i < token.getRow(); i++) {
			index = content.indexOf("\n", index);
		}
		int lastIndex = content.indexOf("\n", index);

		if (index == -1)
			index = 0;
		if (lastIndex == -1)
			lastIndex = content.length();

		String line = content.substring(index, lastIndex);
		out.append(line);
		out.append(" ".repeat(Math.max(0, token.getStartChar() - 2)));
		out.append("^".repeat(token.getValue().length()));
		out.append("\n");
		out.append("─".repeat(Math.max(0, token.getStartChar() - 2)));
		out.append("┘");
		out.append("\n");
		return out.toString();
	}

	private int findNextOf(TokenType... types) {
		int relativeIndex = 0;
		while (iter.hasNext()) {
			Token next = iter.next();
			relativeIndex++;

			if (next.isOfType(types)) {
				iter.rewind(relativeIndex);
				return iter.getIndex() + relativeIndex;
			}
		}
		iter.rewind(relativeIndex);
		return -1;
	}
}
