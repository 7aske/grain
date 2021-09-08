package com._7aske.grain.http.view.ast.parser;

import com._7aske.grain.http.view.ast.*;
import com._7aske.grain.http.view.ast.lexer.Lexer;
import com._7aske.grain.http.view.ast.lexer.Token;
import com._7aske.grain.http.view.ast.parser.exception.ParserOperationNotSupportedException;
import com._7aske.grain.http.view.ast.parser.exception.ParserSyntaxErrorException;
import com._7aske.grain.http.view.ast.types.AstBooleanOperator;
import com._7aske.grain.http.view.ast.types.AstEqualityOperator;
import com._7aske.grain.http.view.ast.types.AstLiteralType;

import java.util.ArrayList;
import java.util.List;

import static com._7aske.grain.http.view.ast.lexer.TokenType.*;

public class Parser {
	private final Lexer lexer;
	private final List<Token> tokens;
	private final List<AstNode> parsed;
	private final TokenIterator iterator;
	private AstNode prevNode;
	private Token prevToken;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.tokens = lexer.getTokens();
		this.iterator = new TokenIterator(tokens);
		this.parsed = new ArrayList<>();
	}

	public List<AstNode> parse() {
		while(iterator.hasNext()) {
			AstNode node = parseExpression();
			parsed.add(node);
		}
		return parsed;
	}

	private AstNode parseExpression() {
		if (!iterator.hasNext())
			return null;
		AstNode node = null;

		Token curr = iterator.next();
		AstNode currNode = createNode(curr);

		if (iterator.isPeekOfType(ASSN)) {
			iterator.next(); // move to ASSN
			AstAssignmentNode newNode = new AstAssignmentNode();
			if (!(currNode instanceof AstSymbolNode)) {
				Token error = curr;
				printSourceCodeLocation(error);
				throw new ParserSyntaxErrorException("Unable to assign to '%s'", error.getType());
			}
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
			iterator.next(); // move to IF
			node = getAstIfNode();
		} else if (iterator.isPeekOfType(EQ)) {
			Token next = iterator.next();
			AstEqualityNode newNode = new AstEqualityNode();
			newNode.setOperator(AstEqualityOperator.from(next.getType()));
			newNode.setLeft(currNode);
			newNode.setRight(parseExpression());
			node = newNode;
		}  else {
			if (curr.isOfType(IF)) {
				node = getAstIfNode();
			} else if (curr.isOfType(SCOL)) {
				return currNode;
			} else if (curr.isOfType(LBRACE)) {
				while(!iterator.isPeekOfType(RBRACE)) {
					((AstBlockNode)currNode).addNode(parseExpression());
				}
				iterator.next();
				node = currNode;
			} else {
				node = currNode;
			}
		}

		return node;
	}

	private AstIfNode getAstIfNode() {
		AstIfNode newNode = new AstIfNode();
		if (!iterator.isPeekOfType(LPAREN)) {
			printSourceCodeLocation(iterator.peek());
			throw new ParserSyntaxErrorException("Unexpected token '%s' %s", iterator.peek().getType(), iterator.peek().getInfo());
		}

		iterator.next(); // skip LPAREN

		newNode.setCondition(parseExpression());
		if (!iterator.isPeekOfType(RPAREN)) {
			Token error = iterator.next();
			printSourceCodeLocation(error);
			throw new ParserSyntaxErrorException("Expected token ')' got '%s' %s", error.getType(), error.getInfo());
		}

		iterator.next(); // skip RPAREN

		if (!iterator.isPeekOfType(LBRACE)){
			Token error = iterator.next();
			printSourceCodeLocation(error);
			throw new ParserSyntaxErrorException("Expected token '{' got '%s' %s", error.getValue(), error.getInfo());
		}

		newNode.setIfTrue(parseExpression());

		if (iterator.isPeekOfType(ELSE)) {
			iterator.next();
			newNode.setIfFalse(parseExpression());
		}
		return newNode;
	}

	private AstNode createNode(Token token) {
		AstNode created = doCreateNode(token);
		prevNode = created;
		prevToken = token;
		return created;
	}

	private AstNode doCreateNode(Token token) {
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
		for (int i = 1; i < token.getStartChar() - 1; i++) {
			System.err.print(" ");
		}
		for (int i = 0; i < token.getValue().length(); i++) {
			System.err.print("^");
		}
		System.err.println();
		for (int i = 1; i < token.getStartChar() - 1; i++) {
			System.err.print("─");
		}
		System.err.print("┘");

		System.err.println();
	}
}
