package com._7aske.grain.component;

import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.util.HttpPathUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class ControllerWrapper {
	private final Object controller;
	private final RequestMapping mapping;

	public ControllerWrapper(Object controller) {
		this.controller = controller;
		this.mapping = controller.getClass().getAnnotation(RequestMapping.class);
	}

	public Optional<ControllerMethodWrapper> getHandlerForPathAndMethod(String path, HttpMethod method){
		return Arrays.stream(controller.getClass().getMethods())
				.filter(m -> m.isAnnotationPresent(RequestMapping.class))
				.filter(m -> {
					RequestMapping methodMapping = m.getAnnotation(RequestMapping.class);
					return HttpPathUtil.arePathsMatching(path, mapping.value() + methodMapping.value())
							&& methodMapping.method().equals(method);
				})
				.map(ControllerMethodWrapper::new)
				.findFirst();
	}

	public Object getInstance() {
		return this.controller;
	}
}
