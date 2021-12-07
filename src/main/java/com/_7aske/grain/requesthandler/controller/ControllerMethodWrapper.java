package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static com._7aske.grain.util.HttpPathUtil.PATH_VARIABLE_PATTERN;
import static com._7aske.grain.util.ReflectionUtil.getAnnotatedHttpMethod;
import static com._7aske.grain.util.ReflectionUtil.getAnnotatedHttpPath;

public class ControllerMethodWrapper {
	private final Method method;
	private final String path;
	private final HttpMethod httpMethod;
	// Pattern matching for path variables.
	private final List<String> pathVariables = new ArrayList<>();

	public ControllerMethodWrapper(Method method) {
		this.method = method;
		this.method.setAccessible(true);

		this.httpMethod = getAnnotatedHttpMethod(method);
		this.path = getAnnotatedHttpPath(method);

		Matcher matcher = PATH_VARIABLE_PATTERN.matcher(this.path);
		while (matcher.find()) {
			this.pathVariables.add(matcher.group(2));
		}
	}

	public Object invoke(Object instance, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException | InvocationTargetException | HttpException e) {
			if (e.getCause() instanceof HttpException) {
				throw (HttpException) e.getCause();
			}
			if (e instanceof InvocationTargetException) {
				throw (RuntimeException) e.getCause();
			}
			throw new HttpException.InternalServerError(e, path);
		}
	}

	public List<String> getPathVariables() {
		return pathVariables;
	}

	public String getPathVariable(int index) {
		return pathVariables.get(index);
	}

	public int getParameterCount() {
		return method.getParameterCount();
	}

	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public Parameter[] getParameters() {
		return method.getParameters();
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getPath() {
		return path;
	}

}
