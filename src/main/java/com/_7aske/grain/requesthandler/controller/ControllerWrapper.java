package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.web.controller.annotation.RequestMapping;
import com._7aske.grain.http.HttpMethod;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com._7aske.grain.util.HttpPathUtil.arePathsMatching;
import static com._7aske.grain.util.HttpPathUtil.join;
import static com._7aske.grain.util.ReflectionUtil.*;

public class ControllerWrapper {
	private final Object controller;
	private final String httpPath;
	private final List<ControllerMethodWrapper> methods;

	public ControllerWrapper(Object controller) {
		this.controller = controller;
		// HttpMethod is ignored for controllers
		this.httpPath = getAnnotatedHttpPath(controller.getClass());
		this.methods = Arrays.stream(controller.getClass().getMethods())
				// Must call ReflectionUtil#isAnnotationPresent as it checks
				// for the presence of annotations in the provided parameter
				// and its annotations.
				.filter(m -> isAnnotationPresent(m, RequestMapping.class))
				.map(ControllerMethodWrapper::new)
				.collect(Collectors.toList());
	}

	public List<ControllerMethodWrapper> getMethods() {
		return methods;
	}

	public Optional<ControllerMethodWrapper> getMethod(String requestPath, HttpMethod method) {
		return methods.stream()
				// matching request path and controller + method paths
				.filter(m -> arePathsMatching(requestPath, join(getPath(), m.getPath()))
						&& (method == null || m.getHttpMethod().equals(method)))
				.max(Comparator.comparingInt(p -> p.getPath().length()));
	}

	public Object getInstance() {
		return this.controller;
	}

	public String getPath() {
		return httpPath != null ? httpPath : "/";
	}
}
