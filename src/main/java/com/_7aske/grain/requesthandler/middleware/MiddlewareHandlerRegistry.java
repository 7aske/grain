package com._7aske.grain.requesthandler.middleware;

import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Inject;
import com._7aske.grain.core.component.Priority;
import com._7aske.grain.web.controller.annotation.RequestMapping;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.util.HttpPathUtil;

import java.util.List;
import java.util.stream.Collectors;

@Grain
public class MiddlewareHandlerRegistry implements HandlerRegistry {
	@Inject
	private DependencyContainer container;

	// @Incomplete allow setting path for middleware classes
	@Override
	public boolean canHandle(String path, HttpMethod method) {
		return true;
	}

	public List<Handler> getHandlers(String path, HttpMethod method) {
		return container.getGrains(Middleware.class)
				.stream()
				.sorted(((o1, o2) -> {
					if (o1.getClass().isAnnotationPresent(Priority.class) && o2.getClass().isAnnotationPresent(Priority.class)) {
						Priority p1 = o1.getClass().getAnnotation(Priority.class);
						Priority p2 = o2.getClass().getAnnotation(Priority.class);
						return -Integer.compare(p1.value(), p2.value());
					}
					return 0;
				}))
				.filter(m -> {
					if (m.getClass().isAnnotationPresent(RequestMapping.class)) {
						RequestMapping mapping = m.getClass().getAnnotation(RequestMapping.class);
						return HttpPathUtil.arePathsMatching(path, mapping.value())
								&& (method == null || method.equals(mapping.method()));
					}
					return true;
				})
				.map(Handler.class::cast)
				.collect(Collectors.toList());
	}
}
