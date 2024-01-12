package com._7aske.grain.core.component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

public interface DependencyContainer {
	void registerGrain(Object grain);

	<T> T getGrain(Class<T> clazz);

	<T> T getGrain(String name);

	Collection<Object> getGrainsAnnotatedBy(Class<? extends Annotation> clazz);

	<T> Optional<T> getOptionalGrain(Class<T> clazz);

	<T> Optional<T> getOptionalGrain(String name);

	<T> Collection<T> getGrains(Class<T> clazz);
}
