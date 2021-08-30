package com._7aske.grain.component;

import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.util.HttpPathUtil;

import java.util.Optional;
import java.util.Set;

public class ControllerRegistry {
	private final GrainRegistry grainRegistry;

	public ControllerRegistry(GrainRegistry grainRegistry) {
		this.grainRegistry = grainRegistry;
	}

	public Set<Object> getControllers() {
		return grainRegistry.getControllers();
	}


	public Optional<ControllerWrapper> getControllerForPath(String path) {
		return getControllers()
				.stream()
				.filter(c -> c.getClass().isAnnotationPresent(RequestMapping.class))
				.filter(c -> {
					RequestMapping mapping = c.getClass().getAnnotation(RequestMapping.class);
					return HttpPathUtil.arePathsMatching(path, mapping.path());
				})
				.map(ControllerWrapper::new)
				.findFirst();
	}
}
