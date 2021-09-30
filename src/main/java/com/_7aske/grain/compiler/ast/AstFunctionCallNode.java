package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class AstFunctionCallNode extends AstUnaryNode {
	private Object returnValue;

	@FunctionalInterface
	public interface AstFunctionCallback {
		Object call(Object... args);
	}

	private AstSymbolNode symbol;
	private List<AstNode> arguments;
	private AstFunctionCallback callback;
	private Object backReference;
	private Object reference;

	public AstFunctionCallNode() {
	}

	public AstSymbolNode getSymbol() {
		return symbol;
	}

	public List<AstNode> getArguments() {
		return arguments;
	}

	public void setSymbol(AstSymbolNode symbol) {
		this.symbol = symbol;
	}

	public void setArguments(List<AstNode> arguments) {
		this.arguments = arguments;
	}

	public AstFunctionCallback getCallback() {
		return callback;
	}

	public void setCallback(AstFunctionCallback callback) {
		this.callback = callback;
	}

	public Object getBackReference() {
		return backReference;
	}

	public void setBackReference(Object backReference) {
		this.backReference = backReference;
	}

	public Object getReference() {
		return reference;
	}

	public void setReference(Object reference) {
		this.reference = reference;
	}

	@Override
	public void run(Interpreter interpreter) {
		for (AstNode arg : arguments) {
			arg.run(interpreter);
		}

		if (this.backReference != null) {
			AstFunctionCallNode.AstFunctionCallback callback = (args) -> {
				try {
					Class<?> clazz = AstFunctionCallNode.this.backReference instanceof Class<?> ? (Class<?>)
							AstFunctionCallNode.this.backReference : AstFunctionCallNode.this.backReference.getClass();
					Method method = getMethod(clazz, getSymbol().getName(), args);
					return method.invoke(this.backReference, args);
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
			};
			this.setCallback(callback);
		} else {
			this.setCallback((AstFunctionCallback) interpreter.getSymbolValue(this.getSymbol().getName()));
		}
		this.returnValue = callback.call(arguments.stream().map(AstNode::value).toArray(Object[]::new));

		if (this.reference instanceof AstFunctionCallNode){
			((AstFunctionCallNode) this.reference).setBackReference(this.returnValue);
		} else if (this.reference instanceof AstObjectReferenceNode){
			((AstObjectReferenceNode) this.reference).setBackReference(this.returnValue);
		}
		if (this.reference instanceof AstNode) {
			((AstNode) this.reference).run(interpreter);
			this.returnValue = ((AstNode) this.reference).value();
		}
	}

	private Method getMethod(Class<?> clazz, String methodName, Object[] args) throws NoSuchMethodException {
		Method[] methods = clazz.getMethods();
		return Arrays.stream(methods)
				.filter(m -> m.getName().equals(methodName) && areAllParametersCastable(m.getParameters(), args))
				.findFirst()
				.orElseThrow(NoSuchMethodException::new);
	}

	// TODO: implemented finding varargs methods
	private boolean areAllParametersCastable(Parameter[] parameters, Object[] args) {
		if (parameters.length != args.length) return false;
		for (int i = 0; i < parameters.length; i++) {
			if (!parameters[i].getType().isAssignableFrom(args[i].getClass())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Object value() {
		return returnValue;
	}

}
