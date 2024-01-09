package com._7aske.grain.web.requesthandler.controller.wrapper;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.HttpPathUtil;
import com._7aske.grain.util.ReflectionUtil;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static com._7aske.grain.util.HttpPathUtil.PATH_VARIABLE_PATTERN;
import static com._7aske.grain.util.ReflectionUtil.getAnnotatedHttpMethods;
import static com._7aske.grain.util.ReflectionUtil.getAnnotatedHttpPath;

/**
 * Wrapper around a controller Grain component method responsible for handling {@link HttpRequest}s.
 */
public class ControllerMethodWrapper {
	private final Method method;
	private final String path;
	private final List<HttpMethod> httpMethods;
	private final Object controllerInstance;
	private final Logger logger;

	public ControllerMethodWrapper(Method method, Object controllerInstance) {
		this.method = method;
		this.controllerInstance = controllerInstance;
		this.logger = LoggerFactory.getLogger(method.getDeclaringClass());

		this.httpMethods = List.of(getAnnotatedHttpMethods(method));
		this.path = HttpPathUtil.join(getAnnotatedHttpPath(method.getDeclaringClass()), getAnnotatedHttpPath(method));

		validatePathVariables();
	}

	public Object invoke(Object... args) {
		try {
			logger.trace("Invoking method {}", method.getName());
			return ReflectionUtil.invokeMethod(method, controllerInstance, args);
		} catch (Exception e) {
			if (e.getCause() instanceof HttpException) {
				throw (HttpException) e.getCause();
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

	private void validatePathVariables() {
		final List<String> pathVariables = new ArrayList<>();
		Matcher matcher = PATH_VARIABLE_PATTERN.matcher(this.path);
		while (matcher.find()) {
			pathVariables.add(matcher.group(2));
		}

		long pathVariablesDeclared = Arrays.stream(method.getParameters())
				.filter(p -> p.isAnnotationPresent(com._7aske.grain.web.controller.annotation.PathVariable.class))
				.count();
		int pathVariablesSize = pathVariables.size();

		if (pathVariablesDeclared > pathVariablesSize) {
			throw new GrainRuntimeException("Declared path variables > path variables in method " + method.getName() + " in controller " + method.getDeclaringClass().getName());
		}
		if (pathVariablesDeclared < pathVariablesSize) {
			logger.warn("Path variables != declared variables in method {}", method.getName());
		}
	}
}
