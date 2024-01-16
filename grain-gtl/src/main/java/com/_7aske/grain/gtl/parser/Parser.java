package com._7aske.grain.gtl.parser;

import com._7aske.grain.gtl.ast.*;
import com._7aske.grain.gtl.ast.basic.AstBinaryNode;
import com._7aske.grain.gtl.ast.basic.AstNode;
import com._7aske.grain.gtl.ast.basic.AstTernaryNode;
import com._7aske.grain.gtl.ast.types.*;
import com._7aske.grain.gtl.lexer.Lexer;
import com._7aske.grain.gtl.lexer.Token;
import com._7aske.grain.gtl.lexer.TokenType;
import com._7aske.grain.gtl.parser.exception.ParserOperationNotSupportedException;
import com._7aske.grain.gtl.parser.exception.ParserSyntaxErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com._7aske.grain.gtl.lexer.TokenType.*;

public class Parser {
	public static final String FRAGMENT_START = "@";
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
		}
		if (iter.isPeekOfType(EQ, NE)) {
			node = parseEqualityNode(iter.next(), node);
		}
		if (iter.isPeekOfType(GT, LT, GE, LE)) {
			node = parseRelationalNode(iter.next(), node);
		}
		if (iter.isPeekOfType(ADD, SUB, DIV, MOD, MUL)) {
			node = parseArithmeticNode(iter.next(), node);
		}
		if (iter.isPeekOfType(ASSN)) {
			node = parseAssignmentNode(iter.next(), node);
		}
		if (iter.isPeekOfType(DFLT)) {
			node = parseDefaultNode(iter.next(), node);
		}
		if (iter.isPeekOfType(TERNCOND)) {
			node = parseTernaryOperatorNode(iter.next(), node);
		}

		parsedStack.push(node);
		return node;
	}

	private AstNode parseSubExpression(int precedance) {
		Token curr = iter.next();
		AstNode node = null;

		if (iter.isPeekOfType(AND, OR)) {
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
		} else if (iter.isPeekOfType(TERNCOND)) {
			node = createNode(curr);
		} else if (iter.isPeekOfType(ASSN)) {
			if (!curr.isOfType(IDEN))
				throw new ParserSyntaxErrorException(getSourceCodeLocation(curr),
						"Cannot assign to '%s'", curr.getType());
			node = createNode(curr);
			node = parseAssignmentNode(iter.next(), node);
		} else if (curr.isOfType(INCLUDE)) {
			node = parseIncludeStatement(curr);
		} else if (curr.isOfType(IDEN) && iter.isPeekOfType(LPAREN)) {
			node = createNode(curr);
			if (((AstSymbolNode) node).getName().startsWith(FRAGMENT_START)) {
				node = parseFragmentCall((AstSymbolNode) node);
			} else {
				node = parseFunctionCall((AstSymbolNode) node);
			}
		} else if (curr.isOfType(IDEN) && iter.isPeekOfType(DOT)) {
			node = parseObject(curr);
		} else if (curr.isOfType(IDEN) && iter.isPeekOfType(LBRACK)) {
			node = parseArrayIndex(curr);
		} else if (iter.isPeekOfType(SCOL, COMMA)) {
			node = createNode(curr);
			iter.next();
		} else if (iter.isPeekOfType(RPAREN)) {
			node = createNode(curr);
		} else if (iter.isPeekOfType(RBRACK)) {
			node = createNode(curr);
			iter.next();
		} else if (curr.isOfType(LPAREN)) {
			// we hope that last value will be on the stack
			// so that createNode() will pick it up from inside the next
			// parseSubExpression() call
			parseExpression();
			node = parseSubExpression(10000);
		} else if (curr.isOfType(COMMA)) {
			node = parseExpression();
		} else if (curr.isOfType(IMPORT)) {
			node = parseImportStatement(curr);
		} else if (curr.isOfType(ADD)) {
			node = parseSubExpression(precedance);
		} else if (curr.isOfType(SUB)) {
			node = parseMinus();
		} else if (curr.isOfType(NOT)) {
			AstNotNode astNotNode = (AstNotNode) createNode(curr);
			AstNode parsed = parseSubExpression(AstNotNode.PRECEDENCE);
			astNotNode.setNode(parsed);
			node = astNotNode;
		} else if (curr.isOfType(TERNCOND)) {
			node = parsedStack.pop();
			node = parseTernaryOperatorNode(curr, node);
		} else {
			node = createNode(curr);
		}

		parsedStack.push(node);
		return node;
	}


	private AstNode parseMinus() {
		AstMinusNode astMinusNode = new AstMinusNode();
		AstNode parsed;
		if (iter.isPeekOfType(LPAREN)) {
			parsed = parseSubExpression(AstMinusNode.PRECEDENCE);
		} else {
			parsed = createNode(iter.next());
		}
		astMinusNode.setNode(parsed);
		return astMinusNode;
	}

	private AstNode parseImportStatement(Token curr) {
		AstImportNode astImportNode = (AstImportNode) createNode(curr);
		if (!iter.isPeekOfType(LIT_STR))
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()),
					"Expected token LIT_STR got '%s'", iter.peek().getValue());
		astImportNode.setPackage(parseSubExpression(Integer.MIN_VALUE));
		if (iter.isPeekOfType(AS)) {
			iter.next(); // skip as
			if (!iter.isPeekOfType(IDEN)) {
				throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()),
						"Expected token IDEN got '%s'", iter.peek().getValue());
			}
			astImportNode.setAlias((AstSymbolNode) createNode(iter.next()));
		}
		return astImportNode;
	}

	private AstNode parseIncludeStatement(Token curr) {
		AstIncludeNode astIncludeNode = (AstIncludeNode) createNode(curr);
		AstLiteralNode importPath = (AstLiteralNode) createNode(iter.next());
		if (!iter.isPeekOfType(AS))
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()),
					"Expected token '%s'", AS.getValue());
		iter.next(); // skip AS
		AstSymbolNode astSymbolNode = (AstSymbolNode) createNode(iter.next());
		if (!iter.isPeekOfType(SCOL))
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()),
					"Expected token '%s'", SCOL.getValue());
		iter.next(); // skip SCOL
		astIncludeNode.setPath((String) importPath.getValue());
		astIncludeNode.setIdentifier(astSymbolNode);
		return astIncludeNode;
	}

	private AstNode parseArrayIndex(Token curr) {
		AstArrayIndexNode arrayIndexNode = new AstArrayIndexNode(createNode(curr));
		iter.next(); // skip LBRACK
		arrayIndexNode.setIndex(parseSubExpression(Integer.MIN_VALUE));
		return arrayIndexNode;
	}

	private AstNode fixPrecedence(AstNode start, AstNode right) {
		if (start.getPrecedence() < right.getPrecedence() && start instanceof AstTernaryNode && right instanceof AstBinaryNode) {
			AstTernaryNode startTernary = (AstTernaryNode) start;
			AstBinaryNode rightBinary = (AstBinaryNode) right;

			AstNode temp = rightBinary.getRight();
			rightBinary.setRight(startTernary);
			startTernary.setCondition(temp);

			return right;
		} else if (start.getPrecedence() > right.getPrecedence() && start instanceof AstBinaryNode && right instanceof AstBinaryNode) {
			AstBinaryNode startBinary = (AstBinaryNode) start;
			AstBinaryNode rightBinary = (AstBinaryNode) right;

			AstNode temp = rightBinary.getLeft();
			rightBinary.setLeft(startBinary);
			startBinary.setRight(temp);

			return rightBinary;
		}
		return start;
	}

	private AstNode parseDefaultNode(Token token, AstNode node) {
		AstDefaultNode astDefaultNode = (AstDefaultNode) createNode(token);
		astDefaultNode.setValue(node);
		astDefaultNode.setDefault(parseSubExpression(0));
		return astDefaultNode;
	}

	private AstNode parseAssignmentNode(Token token, AstNode left) {
		AstAssignmentNode astAssignmentNode = (AstAssignmentNode) createNode(token);
		if (iter.isPeekOfType(ASSN))
			iter.next(); // skip assn
		astAssignmentNode.setSymbol(left);
		AstNode value = parseExpression();

		astAssignmentNode.setValue(value);
		return astAssignmentNode;
	}


	private AstNode parseFragmentCall(AstSymbolNode left) {
		iter.next(); // skip LPAREN
		AstFragmentCallNode fragmentCallNode = new AstFragmentCallNode();
		fragmentCallNode.setSymbol(left);
		List<AstNode> arguments = new ArrayList<>();
		while (!iter.isPeekOfType(RPAREN) && !iter.isPeekOfType(SCOL) && !iter.isPeekOfType(_END)) {
			AstNode node = parseExpression();
			// Assignments nodes apparently parse just fine in the as function
			// arguments so, we just modify them to be new keyword arguments
			if (node instanceof AstAssignmentNode) {
				arguments.add(new AstKeywordArgumentNode(((AstAssignmentNode) node).getSymbol(), ((AstAssignmentNode) node).getValue()));
			} else {
				arguments.add(node);
			}
		}
		fragmentCallNode.setArguments(arguments);
		if (iter.isPeekOfType(RPAREN))
			iter.next();
		if (iter.isPeekOfType(SCOL))
			iter.next();
		return fragmentCallNode;
	}

	private AstNode parseFunctionCall(AstSymbolNode left) {
		iter.next(); // skip LPAREN
		AstFunctionCallNode functionCallNode = new AstFunctionCallNode();
		functionCallNode.setSymbol(left);
		List<AstNode> arguments = new ArrayList<>();
		while (!iter.isPeekOfType(RPAREN) && !iter.isPeekOfType(SCOL) && !iter.isPeekOfType(_END)) {
			AstNode node = parseExpression();
			arguments.add(node);
		}
		if (iter.isPeekOfType(RPAREN))
			iter.next();
		functionCallNode.setArguments(arguments);
		if (iter.isPeekOfType(DOT)) {
			iter.next();
			AstNode next = parseSubExpression(Integer.MIN_VALUE);
			functionCallNode.setReference(next);
		}
		if (iter.isPeekOfType(SCOL))
			iter.next();
		return functionCallNode;
	}

	private AstNode parseStatement() {
		AstNode node = null;
		if (iter.isPeekOfType(LBRACE)) {
			node = parseBlockStatement();
		} else if (iter.isPeekOfType(IF)) {
			node = parseIfStatement();
		} else if (iter.isPeekOfType(FOREACH)) {
			node = parseForEachStatement();
		} else if (iter.isPeekOfType(FOR)) {
			node = parseForStatement();
		} else {
			node = parseExpression();
		}
		return node;
	}

	private AstNode parseObject(Token token) {
		if (token.getType() != IDEN)
			throw new ParserSyntaxErrorException(getSourceCodeLocation(token), "Expected identifier got '%s'", token.getValue());

		AstObjectReferenceNode objectNode = new AstObjectReferenceNode(token.getValue());
		iter.next(); // skip DOT
		AstNode ref = parseSubExpression(Integer.MIN_VALUE);
		// If the parsed expression is the instance of ObjectReference or
		// FunctionCall we need to provide it with a backreference
		// for the interpretation step.
		if (ref instanceof AstObjectReferenceNode) {
			objectNode.setReference(ref);
			((AstObjectReferenceNode) ref).setBackReference(objectNode);
		} else if (ref instanceof AstFunctionCallNode) {
			objectNode.setReference(ref);
			((AstFunctionCallNode) ref).setBackReference(objectNode);
		} else if (ref instanceof AstSymbolNode) {
			objectNode.setReference(ref);
		} else {
			// Fix precedence in cases where we are preforming operations
			// on the reference e.g. obj.info.name + 'Test'. In that case
			// parseExpression returns a AstArithmeticNode object for which
			// we need to adjust the left node to point to the root of the
			// object reference chain namely in the example it would be 'obj'.
			if (ref instanceof AstBinaryNode) {
				Object backRef = objectNode.getBackReference();
				do {
					if (backRef instanceof AstObjectReferenceNode) {
						backRef = ((AstObjectReferenceNode) backRef).getBackReference();
					} else if (backRef instanceof AstFunctionCallNode) {
						backRef = ((AstFunctionCallNode) backRef).getBackReference();
					} else {
						backRef = null;
					}
				} while (backRef != null);
				objectNode.setReference(((AstBinaryNode) ref).getLeft());
				((AstBinaryNode) ref).setLeft(objectNode);
				return ref;
			} else {
				throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Unexpected type '%s'", ref.getClass());
			}

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
			forNode.setInitialization(parseSubExpression(Integer.MIN_VALUE));
		if (!iter.isPeekOfType(SCOL))
			forNode.setCondition(parseSubExpression(Integer.MIN_VALUE));
		if (!iter.isPeekOfType(SCOL, RPAREN))
			forNode.setIncrement(parseSubExpression(Integer.MIN_VALUE));
		while (iter.isPeekOfType(RPAREN, SCOL)) {
			iter.next();
		}
		forNode.setBody(parseBlockStatement());
		return forNode;
	}

	private AstNode parseForEachStatement() {
		Token forEachToken = iter.next();
		AstForEachNode forEachNode = (AstForEachNode) createNode(forEachToken);

		// Parenthesis are not necessary
		if (iter.isPeekOfType(LPAREN))
			iter.next(); // skip LPAREN

		if (!iter.isPeekOfType(IDEN))
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Expected '%s'", IDEN.getValue());

		AstForEachIteratorNode astForEachIteratorNode = new AstForEachIteratorNode();
		astForEachIteratorNode.setSymbol((AstSymbolNode) createNode(iter.next()));

		if (!iter.isPeekOfType(IN))
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Expected '%s'", IN.getValue());

		iter.next(); // skip IN
		astForEachIteratorNode.setIterator(parseSubExpression(0));
		forEachNode.setIterator(astForEachIteratorNode);

		// Parenthesis are not necessary
		if (iter.isPeekOfType(RPAREN))
			iter.next(); // skip LPAREN

		forEachNode.setBody(parseBlockStatement());
		return forEachNode;
	}

	private AstNode parseTernaryOperatorNode(Token next, AstNode node) {
		AstTernaryOperatorNode ifNode = (AstTernaryOperatorNode) createNode(next);
		ifNode.setCondition(node);
		AstNode ifTrueNode = parseSubExpression(0);
		ifNode.setIfTrue(ifTrueNode);
		if (!iter.isPeekOfType(TERNELSE)) {
			throw new ParserSyntaxErrorException(getSourceCodeLocation(iter.peek()), "Expected '%s'", TERNELSE.getValue());
		}
		iter.next(); // skip TERNELSE
		AstNode ifFalseNode = parseSubExpression(0);
		ifNode.setIfFalse(ifFalseNode);
		return fixPrecedence(ifNode, ifNode.getCondition());
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
		if (iter.isPeekOfType(LBRACE)) {
			AstBlockNode blockNode = (AstBlockNode) createNode(iter.next());

			while (!iter.isPeekOfType(RBRACE)) {
				AstNode node = parseStatement();
				if (node != null)
					blockNode.addNode(node);
			}
			iter.next(); // skip RBRACE

			return blockNode;
		} else {
			return parseExpression();
		}
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
			case TERNCOND:
				return new AstTernaryOperatorNode();
			case IF:
				return new AstIfNode();
			case ELSE:
				break;
			case FOR:
				return new AstForNode();
			case FOREACH:
				return new AstForEachNode();
			case CONTINUE:
				return new AstContinueNode();
			case BREAK:
				return new AstBreakNode();
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
			case SCOL:
				return new AstExpressionEndNode();
			case LBRACE:
				return new AstBlockNode();
			case IMPORT:
				return new AstImportNode();
			case INCLUDE:
				return new AstIncludeNode();
			case DFLT:
				return new AstDefaultNode();
			case _END:
				throw new ParserSyntaxErrorException(getSourceCodeLocation(token), "Invalid end of expression");
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
		out.append("^".repeat(token.getValue() == null ? 1 : token.getValue().length()));
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
