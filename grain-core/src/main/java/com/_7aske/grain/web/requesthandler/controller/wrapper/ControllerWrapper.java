package com._7aske.grain.web.requesthandler.controller.wrapper;

import com._7aske.grain.web.controller.annotation.Mappings;
import com._7aske.grain.web.controller.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com._7aske.grain.core.reflect.ReflectionUtil.isAnnotationPresent;

/**
 * Wrapper around a controller Grain component instance.
 */
public class ControllerWrapper {
	private final String httpPath;
	private final List<ControllerMethodWrapper> methods;
	private final Object controller;

	/**
	 * @param controller Controller instance annotated with {@link com._7aske.grain.core.component.Controller}
	 *                   annotation.
	 */
	public ControllerWrapper(Object controller) {
		Objects.requireNonNull(controller, "Controller instance cannot be null");
		if (!isAnnotationPresent(controller.getClass(), RequestMapping.class)) {
			throw new IllegalArgumentException("Controller must be annotated with @RequestMapping annotation");
		}
		this.controller = controller;

		// HttpMethod is ignored for controllers
		this.httpPath = Objects.requireNonNull(Mappings.getAnnotatedHttpPath(controller.getClass()));
		this.methods = Arrays.stream(controller.getClass().getMethods())
				// Must call ReflectionUtil#isAnnotationPresent as it checks
				// for the presence of annotations in the provided parameter
				// and its annotations.
				.filter(m -> isAnnotationPresent(m, RequestMapping.class))
				.map((Method method) -> new ControllerMethodWrapper(method, controller))
				.toList();
	}

	/**
	 * @return the list wrappers around the methods of the controller that are
	 * valid request handlers.
	 */
	public List<ControllerMethodWrapper> getMethods() {
		return methods;
	}

	/**
	 * @return path declared by the controller component.
	 */
	public String getPath() {
		return httpPath != null ? httpPath : "/";
	}

	public String getName() {
		return controller.getClass().getSimpleName();
	}
}
