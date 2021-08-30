package com._7aske.grain.component;

import com._7aske.grain.exception.http.HttpException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ControllerMethodWrapper {
	private final Method method;

	public ControllerMethodWrapper(Method method) {
		this.method = method;
	}

	public Object invoke(Object instance, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new HttpException.InternalServerError(e);
		}
	}

	public int getParameterCount() {
		return method.getParameterCount();
	}

	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}
}
