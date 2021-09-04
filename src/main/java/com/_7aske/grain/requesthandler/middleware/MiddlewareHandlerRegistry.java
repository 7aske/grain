package com._7aske.grain.requesthandler.middleware;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.component.Priority;
import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.requesthandler.handler.Handler;
import com._7aske.grain.requesthandler.handler.HandlerRegistry;
import com._7aske.grain.util.HttpPathUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MiddlewareHandlerRegistry implements HandlerRegistry {
	private final Set<Middleware> middlewares;

	public MiddlewareHandlerRegistry(GrainRegistry grainRegistry) {
		this.middlewares = grainRegistry.getMiddlewares()
				.stream()
				.sorted(((o1, o2) -> {
					if (o1.getClass().isAnnotationPresent(Priority.class) && o2.getClass().isAnnotationPresent(Priority.class)) {
						Priority p1 = o1.getClass().getAnnotation(Priority.class);
						Priority p2 = o2.getClass().getAnnotation(Priority.class);
						return -Integer.compare(p1.value(), p2.value());
					}
					return 0;
				}))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public boolean canHandle(String path, HttpMethod method) {
		return true;
	}

	public List<Handler> getHandlers(String path, HttpMethod method) {
		return middlewares.stream()
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


	public void addHandler(Middleware middleware) {
		middlewares.add(middleware);
	}
}
