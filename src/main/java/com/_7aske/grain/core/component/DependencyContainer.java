package com._7aske.grain.core.component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

public interface DependencyContainer {
	<T> T getGrain(Class<T> clazz);

	Collection<Object> getGrainsAnnotatedBy(Class<? extends Annotation> clazz);

	<T> Optional<T> getOptionalGrain(Class<T> clazz);

	<T> Collection<T> getGrains(Class<T> clazz);
}
