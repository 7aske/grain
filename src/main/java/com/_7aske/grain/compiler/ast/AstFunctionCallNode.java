package com._7aske.grain.compiler.ast;

import com._7aske.grain.compiler.ast.basic.AstNode;
import com._7aske.grain.compiler.ast.basic.AstUnaryNode;
import com._7aske.grain.compiler.interpreter.Interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AstFunctionCallNode extends AstUnaryNode {
	private Object returnValue;

	@FunctionalInterface
	public interface AstFunctionCallback {
		Object call(Object... args);
	}

	private Object object;
	private AstSymbolNode symbol;
	private List<AstNode> arguments;
	private AstFunctionCallback callback;

	public AstFunctionCallNode() {
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
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

	@Override
	public void run(Interpreter interpreter) {
		for (AstNode arg : arguments) {
			arg.run(interpreter);
		}
		if (this.object != null) {
			AstFunctionCallNode.AstFunctionCallback callback = (args) -> {
				try {
					Class<?> clazz = this.object instanceof Class<?> ? (Class<?>) this.object : this.object.getClass();
					Method method = clazz.getMethod(this.getSymbol().getName(), Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
					return method.invoke(this.object, args);
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
			};
			this.setCallback(callback);
		} else {
			this.setCallback((AstFunctionCallback) interpreter.getSymbolValue(this.getSymbol().getName()));
		}
		this.returnValue = callback.call(arguments.stream().map(AstNode::value).toArray(Object[]::new));
	}

	private Method getMethod(Class<?> clazz, String methodName, int argCount) throws NoSuchMethodException {
		return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(methodName) && m.getParameterCount() == argCount).findFirst().orElseThrow(NoSuchMethodException::new);
	}

	@Override
	public Object value() {
		return returnValue;
	}

}
