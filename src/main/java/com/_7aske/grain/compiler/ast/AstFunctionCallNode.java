package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;
import com._7aske.grain.compiler.interpreter.exception.InterpreterNoSuchMethodException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class AstFunctionCallNode extends AstUnaryNode {

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
	public Object run(Interpreter interpreter) {
		AstFunctionCallback callback;
		if (this.backReference != null) {
			callback = (args) -> {
				try {
					Class<?> clazz = AstFunctionCallNode.this.backReference instanceof Class<?> ? (Class<?>)
							AstFunctionCallNode.this.backReference : AstFunctionCallNode.this.backReference.getClass();
					Method method = getMethod(clazz, getSymbol().getName(), args);
					method.setAccessible(true);
					return method.invoke(this.backReference, args);
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					throw new InterpreterNoSuchMethodException(e);
				}
			};
		} else {
			Object symbolObject = interpreter.getSymbolValue(this.getSymbol().getName());
			if (symbolObject instanceof Class<?>) {
				callback = (args) -> {
					try {
						Constructor<?> constructor = getConstructor((Class<?>) symbolObject, args);
						return constructor.newInstance(args);
					} catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					}
				};
			} else if (symbolObject instanceof AstFunctionCallback) {
				callback = (AstFunctionCallback) interpreter.getSymbolValue(this.getSymbol().getName());
			} else {
				throw new NullPointerException(String.format("Undefined symbol '%s'", this.symbol.getName()));
			}
		}
		Object[] args = this.arguments.stream()
				.map(arg -> arg.run(interpreter))
				.toArray();

		this.callback = callback;
		Object returnValue = callback.call(args);

		if (this.reference instanceof AstFunctionCallNode){
			((AstFunctionCallNode) this.reference).setBackReference(returnValue);
		} else if (this.reference instanceof AstObjectReferenceNode){
			((AstObjectReferenceNode) this.reference).setBackReference(returnValue);
		}

		if (this.reference instanceof AstNode) {
			returnValue = ((AstNode)this.reference).run(interpreter);
		}

		return returnValue;
	}

	private Method getMethod(Class<?> clazz, String methodName, Object[] args) throws NoSuchMethodException {
		Method[] methods = clazz.getMethods();
		return Arrays.stream(methods)
				.filter(m -> m.getName().equals(methodName) && areAllParametersCastable(m.getParameters(), args))
				.findFirst()
				.orElseThrow(NoSuchMethodException::new);
	}

	private Constructor<?> getConstructor(Class<?> clazz, Object[] args) throws NoSuchMethodException {
		Constructor<?>[] constructors = clazz.getConstructors();
		return Arrays.stream(constructors)
				.filter(s -> areAllParametersCastable(s.getParameters(), args))
				.findFirst()
				.orElseThrow(NoSuchMethodException::new);
	}

	// TODO: implemented finding varargs methods
	private boolean areAllParametersCastable(Parameter[] parameters, Object[] args) {
		if (parameters.length != args.length) return false;
		for (int i = 0; i < parameters.length; i++) {
			if (args[i] == null) continue;
			if (!parameters[i].getType().isPrimitive() && !parameters[i].getType().isAssignableFrom(args[i].getClass())) {
				return false;
			}
		}
		return true;
	}
}
