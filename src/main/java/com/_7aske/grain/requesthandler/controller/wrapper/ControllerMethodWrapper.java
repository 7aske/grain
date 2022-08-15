package com._7aske.grain.requesthandler.controller.wrapper;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.util.HttpPathUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static com._7aske.grain.util.HttpPathUtil.PATH_VARIABLE_PATTERN;
import static com._7aske.grain.util.ReflectionUtil.getAnnotatedHttpMethods;
import static com._7aske.grain.util.ReflectionUtil.getAnnotatedHttpPath;

/**
 * Wrapper around a controller Grain component method responsible for handling {@link com._7aske.grain.http.HttpRequest}s.
 */
public class ControllerMethodWrapper {
	private final Method method;
	private final String path;
	private final List<HttpMethod> httpMethods;
	// Pattern matching for path variables.
	private final List<String> pathVariables = new ArrayList<>();
	private final Object controllerInstance;

	public ControllerMethodWrapper(Method method, Object controllerInstance) {
		this.method = method;
		// @Todo If ReflectionUtil#invokeMethod is used to invoke this
		// is not necessary.
		this.method.setAccessible(true);
		this.controllerInstance = controllerInstance;

		this.httpMethods = List.of(getAnnotatedHttpMethods(method));
		this.path = HttpPathUtil.join(getAnnotatedHttpPath(method.getDeclaringClass()), getAnnotatedHttpPath(method));

		Matcher matcher = PATH_VARIABLE_PATTERN.matcher(this.path);
		while (matcher.find()) {
			this.pathVariables.add(matcher.group(2));
		}
	}

	public Object invoke(Object... args) {
		// @Todo refactor to use ReflectionUtil#invokeMethod
		try {
			return method.invoke(controllerInstance, args);
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

	public Parameter[] getParameters() {
		return method.getParameters();
	}

	public List<HttpMethod> getHttpMethods() {
		return httpMethods;
	}

	public String getPath() {
		return path;
	}

}
