package com._7aske.grain.compiler.parser;

import com._7aske.grain.compiler.ast.*;
import com._7aske.grain.compiler.ast.basic.AstBinaryNode;
import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.types.*;
import com._7aske.grain.compiler.lexer.Lexer;
import com._7aske.grain.compiler.lexer.Token;
import com._7aske.grain.compiler.lexer.TokenType;
import com._7aske.grain.compiler.parser.exception.ParserOperationNotSupportedException;
import com._7aske.grain.compiler.parser.exception.ParserSyntaxErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com._7aske.grain.compiler.lexer.TokenType.*;

public class Parser {
	private final Lexer lexer;
	private TokenIterator iter;
	private final Stack<AstNode> parsedStack = new Stack<>();

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public AstNode parse() {
		if (!lexer.isDone())
			lexer.begin();
		this.iter = new TokenIterator(lexer.getTokens());

		if (!iter.isPeekOfType(_START))
			throw new ParserSyntaxErrorException("Token _START missing at the beginning of code block");
		iter.next();

		AstRootNode program = new AstRootNode();
		while (iter.hasNext() && !iter.isPeekOfType(_END)) {
			AstNode node = parseStatement();
			if (node != null)
				program.addNode(node);
		}
		return program;
	}

	private AstNode parseExpression() {
		AstNode node = parseSubExpression(Integer.MIN_VALUE);

		if (iter.isPeekOfType(AND, OR)) {
			node = parseBooleanNode(iter.next(), node);
		} else if (iter.isPeekOfType(EQ, NE)) {
			node = parseEqualityNode(iter.next(), node);
		} else if (iter.isPeekOfType(GT, LT, GE, LE)) {
			node = parseRelationalNode(iter.next(), node);
		} else if (iter.isPeekOfType(ADD, SUB, DIV, DIV, MUL)) {
			node = parseArithmeticNode(iter.next(), node);
		}
		return node;
	}

	private AstNode parseSubExpression(int precedance) {
		Token curr = iter.next();
		AstNode node = null;

		if (iter.isPeekOfType(RPAREN)) {
			node = createNode(curr);
			iter.next();
		} else if (curr.isOfType(LPAREN, COMMA)) {
			node = parseExpression();
		} else if (iter.isPeekOfType(AND, OR)) {
			node = createNode(curr);
			node = parseBooleanNode(iter.next(), node);
		} else if (iter.isPeekOfType(EQ, NE)) {
			node = createNode(curr);
			node = parseEqualityNode(iter.next(), node);
		} else if (iter.isPeekOfType(GT, LT, GE, LE)) {
			node = createNode(curr);
			node = parseRelationalNode(iter.next(), node);
		} else if (iter.isPeekOfType(ADD, SUB, DIV, DIV, MUL)) {
			node = createNode(curr);
			node = parseArithmeticNode(iter.next(), node);
		} else if (iter.isPeekOfType(ASSN)) {
			if (!curr.isOfType(IDEN))
				throw new ParserSyntaxErrorException(getSourceCodeLocation(curr),
						"Cannot assign to '%s'", curr.getType());
			node = parseAssignmentNode(curr);
		} else if (curr.isOfType(IDEN) && iter.isPeekOfType(LPAREN)) {
			iter.rewind();
			node = parseFunctionCall();
		} else if (curr.isOfType(IDEN) && iter.isPeekOfType(DOT)) {
			node = parseObject(curr);
		} else if (iter.isPeekOfType(SCOL, COMMA)) {
			node = createNode(curr);
			iter.next();
		} else {
			if (curr.isOfType(NOT)) {
				AstNotNode astNotNode = (AstNotNode) createNode(curr);
				AstNode parsed = parseSubExpression(AstNotNode.PRECEDENCE);
				astNotNode.setNode(parsed);
				node = astNotNode;
			} else {
				node = createNode(curr);
			}
		}

		return node;
	}

	private AstNode fixPrecedence(AstNode start, AstNode right) {
		if (start.getPrecedence() > right.getPrecedence() && start instanceof AstBinaryNode && right instanceof AstBinaryNode) {
			AstBinaryNode startBinary = (AstBinaryNode) start;
			AstBinaryNode rightBinary = (AstBinaryNode) right;

			AstNode temp = rightBinary.getLeft();
			rightBinary.setLeft(startBinary);
			startBinary.setRight(temp);

			return rightBinary;
		}
		return start;
	}

	private AstNode parseAssignmentNode(Token token) {
		AstAssignmentNode astAssignmentNode = (AstAssignmentNode) createNode(iter.peek());
		AstSymbolNode symbolNode = (AstSymbolNode) createNode(token);
		astAssignmentNode.setSymbol(symbolNode);
		iter.next(); // skip assn
		AstNode value = parseSubExpression(AstAssignmentNode.PRECEDENCE);
		astAssignmentNode.setValue(value);
		return astAssignmentNode;
	}

	private AstNode parseFunctionCall() {
		Token token = iter.next();
		iter.next(); // skip LPAREN
		AstFunctionCallNode functionCallNode = new AstFunctionCallNode();
		functionCallNode.setSymbol((AstSymbolNode) createNode(token));
		List<AstNode> arguments = new ArrayList<>();
		while (!iter.isPeekOfType(RPAREN) && !iter.isPeekOfType(SCOL) && !iter.isPeekOfType(_END)) {
			AstNode node = parseExpression();
			arguments.add(node);
		}
		if (iter.isPeekOfType(RPAREN))
			iter.next();
		functionCallNode.setArguments(arguments);
		return functionCallNode;
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

	private AstNode parseObject(Token token) {
		AstObjectReferenceNode objectNode = new AstObjectReferenceNode(token.getValue());
		iter.next(); // skip DOT
		AstNode ref = parseSubExpression(Integer.MIN_VALUE);
		if (ref instanceof AstObjectReferenceNode) {
			objectNode.setReference(ref);
		} else if (ref instanceof AstSymbolNode) {
			objectNode.setReference(ref);
		} else if (ref instanceof AstFunctionCallNode) {
			objectNode.setReference(ref);
		} else {
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Expected '%s' or '%s' but found '%s'",
					AstSymbolNode.class, AstFunctionCallNode.class, ref.getClass());
		}
		return objectNode;
	}

	private AstNode parseArithmeticNode(Token token, AstNode left) {
		AstArithmeticNode arithmeticNode = (AstArithmeticNode) createNode(token);
		arithmeticNode.setLeft(left);
		arithmeticNode.setRight(parseSubExpression(arithmeticNode.getOperator().getPrecedance()));
		return fixPrecedence(arithmeticNode, arithmeticNode.getRight());
	}

	private AstNode parseBooleanNode(Token token, AstNode left) {
		AstBooleanNode booleanNode = (AstBooleanNode) createNode(token);
		booleanNode.setLeft(left);
		booleanNode.setRight(parseSubExpression(booleanNode.getOperator().getPrecedence()));
		return fixPrecedence(booleanNode, booleanNode.getRight());
	}

	private AstNode parseEqualityNode(Token token, AstNode left) {
		AstEqualityNode equalityNode = (AstEqualityNode) createNode(token);
		equalityNode.setLeft(left);
		equalityNode.setRight(parseSubExpression(equalityNode.getOperator().getPrecedance()));
		return fixPrecedence(equalityNode, equalityNode.getRight());
	}

	private AstNode parseRelationalNode(Token token, AstNode left) {
		AstRelationalNode relationalNode = (AstRelationalNode) createNode(token);
		relationalNode.setLeft(left);
		relationalNode.setRight(parseSubExpression(relationalNode.getOperator().getPrecedence()));
		return fixPrecedence(relationalNode, relationalNode.getRight());
	}

	private AstNode parseForStatement() {
		Token forToken = iter.next();
		AstForNode forNode = (AstForNode) createNode(forToken);
		if (!iter.isPeekOfType(LPAREN))
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Expected '%s'", LPAREN.getValue());
		iter.next();

		if (!iter.isPeekOfType(SCOL))
			forNode.setInitialization(parseExpression());
		if (!iter.isPeekOfType(SCOL))
			forNode.setCondition(parseExpression());
		if (!iter.isPeekOfType(SCOL, RPAREN))
			forNode.setIncrement(parseExpression());
		while (iter.isPeekOfType(RPAREN, SCOL)) {
			iter.next();
		}
		forNode.setBody(parseBlockStatement());
		return forNode;
	}

	private AstNode parseIfStatement() {
		Token ifToken = iter.next();
		if (!iter.isPeekOfType(LPAREN)) {
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Expected '%s'", LPAREN.getValue());
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
				return new AstForNode();
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
			case SUB:
			case DIV:
			case MUL:
			case MOD:
				return new AstArithmeticNode(AstArithmeticOperator.from(token.getType()));
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
