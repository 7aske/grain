package com._7aske.grain.web.requesthandler.controller.wrapper;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.util.HttpPathUtil;
import com._7aske.grain.web.controller.annotation.Mappings;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static com._7aske.grain.web.util.HttpPathUtil.PATH_VARIABLE_PATTERN;

/**
 * Wrapper around a controller Grain component method responsible for handling {@link HttpRequest}s.
 */
public class ControllerMethodWrapper extends AbstractControllerMethodWrapper {
	private final String path;
	private final List<HttpMethod> httpMethods;

	public ControllerMethodWrapper(Method method, Object controllerInstance) {
        super(method, controllerInstance);
		this.httpMethods = List.of(Mappings.getAnnotatedHttpMethods(method));
		this.path = HttpPathUtil.join(Mappings.getAnnotatedHttpPath(method.getDeclaringClass()), Mappings.getAnnotatedHttpPath(method));
		validatePathVariables();
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

	public String getName() {
		return this.method.getDeclaringClass().getName() + "#" + this.method.getName();
	}
}
