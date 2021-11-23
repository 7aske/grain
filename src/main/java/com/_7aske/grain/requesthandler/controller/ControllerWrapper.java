package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.http.HttpMethod;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com._7aske.grain.util.HttpPathUtil.arePathsMatching;
import static com._7aske.grain.util.HttpPathUtil.join;

public class ControllerWrapper {
	private final Object controller;
	private final RequestMapping mapping;
	private final List<ControllerMethodWrapper> methods;

	public ControllerWrapper(Object controller) {
		this.controller = controller;
		this.mapping = controller.getClass().getAnnotation(RequestMapping.class);
		this.methods = Arrays.stream(controller.getClass().getMethods())
				.filter(m -> m.isAnnotationPresent(RequestMapping.class))
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

	public Optional<ControllerMethodWrapper> getMethod(String path) {
		return getMethod(path, null);
	}

	public Object getInstance() {
		return this.controller;
	}


	public String getPath() {
		return mapping != null ? mapping.value() : "";
	}

	public RequestMapping getMapping() {
		return mapping;
	}

	public boolean hasMapping() {
		return mapping != null;
	}
}
