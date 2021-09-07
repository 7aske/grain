package com._7aske.grain.http.view.ast.parser;

import com._7aske.grain.http.view.ast.*;
import com._7aske.grain.http.view.ast.lexer.Token;
import com._7aske.grain.http.view.ast.parser.exception.ParserOperationNotSupportedException;
import com._7aske.grain.http.view.ast.parser.exception.ParserSyntaxErrorException;
import com._7aske.grain.http.view.ast.types.AstBooleanOperator;
import com._7aske.grain.http.view.ast.types.AstEqualityOperator;
import com._7aske.grain.http.view.ast.types.AstLiteralType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com._7aske.grain.http.view.ast.lexer.TokenType.*;

public class Parser {
	private final List<Token> tokens;
	private final TokenIterator iterator;
	private final Map<String, AstSymbolNode> symbolTable;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.iterator = new TokenIterator(tokens);
		this.symbolTable = new HashMap<>();
	}

	public AstNode parse() {
		return parseExpression();
	}

	private AstNode parseExpression() {
		if (!iterator.hasNext())
			return null;
		AstNode node = null;

		Token curr = iterator.next();
		if (curr.getType().equals(RPAREN) || curr.getType().equals(RBRACE) || curr.getType().equals(LBRACE)) {
			// TODO: not good
			return parseExpression();
		}
		AstNode currNode = createNode(curr);


		if (iterator.isPeekOfType(ASSN)) {
			Token next = iterator.next();
			AstAssignmentNode newNode = new AstAssignmentNode();
			newNode.setSymbol((AstSymbolNode) currNode);
			newNode.setValue(parseExpression());
			node = newNode;
		} else if (iterator.isPeekOfType(AND, OR)) {
			Token next = iterator.next();
			AstBooleanNode newNode = new AstBooleanNode();
			newNode.setOperator(AstBooleanOperator.from(next.getType()));
			newNode.setLeft(currNode);
			newNode.setRight(parseExpression());
			node = newNode;
		} else if (iterator.isPeekOfType(IF)) {
			Token next = iterator.next();
			AstIfNode newNode = new AstIfNode();
			if (!iterator.peek().getType().equals(LPAREN)) {
				throw new ParserSyntaxErrorException("Unexpected token '" + iterator.peek().getType() + "' " + iterator.peek().getInfo());
			}
			iterator.next();
			newNode.setCondition(parseExpression());
			newNode.setIfTrue(parseExpression());

			if (iterator.isPeekOfType(RBRACE)) {
				iterator.next();
			}
			if (iterator.isPeekOfType(ELSE)) {
				iterator.next();
				newNode.setIfFalse(parseExpression());
			}

			node = newNode;
		} else if (iterator.isPeekOfType(EQ)) {
			Token next = iterator.next();
			AstEqualityNode newNode = new AstEqualityNode();
			newNode.setOperator(AstEqualityOperator.from(next.getType()));
			newNode.setLeft(currNode);
			newNode.setRight(parseExpression());
			node = newNode;
		}  else {
			node = currNode;
		}

		return node;
	}

	private AstNode createNode(Token token) {
		switch (token.getType()) {
			case IDEN:
				return new AstSymbolNode(token.getValue());
			case LIT_STR:
			case LIT_INT:
			case LIT_FLT:
				return new AstLiteralNode(AstLiteralType.from(token.getType()), token.getValue());
			case IF:
				return new AstIfNode();
			case ELIF:
				break;
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
				break;
			case OR:
				break;
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
				break;
			case LPAREN:
				break;
			case RPAREN:
				break;
			case LBRACE:
				break;
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
}
