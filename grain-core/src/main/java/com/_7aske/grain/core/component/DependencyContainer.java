package com._7aske.grain.core.component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

/**
 * Represents a container that holds all components(grains) of the application.
 */
public interface DependencyContainer {

	/**
	 * Registers a grain in the container running it through the dependency injection process.
	 * @param grain grain to register.
	 */
	void registerGrain(Object grain);

	/**
	 * Gets a grain matching the class provided or any of its inheritors or implementors.
	 *
	 * @param clazz class to match.
	 * @return the grain.
	 * @param <T> type of the grain.
	 */
	<T> T getGrain(Class<T> clazz);

	/**
	 * Gets a grain matching the name provided.
	 *
	 * @param name name of the grain.
	 * @return the grain.
	 * @param <T> type of the grain.
	 */
	<T> T getGrain(String name);

	/**
	 * Gets all grains that are annotated with the provided annotation.
	 *
	 * @param clazz annotation to match.
	 * @return collection of grains.
	 */
	Collection<Object> getGrainsAnnotatedBy(Class<? extends Annotation> clazz);

	/**
	 * Gets a grain that is matching the class provided or any of its inheritors or implementors.
	 *
	 * @param clazz annotation to match.
	 * @return the grain.
	 * @param <T> type of the grain.
	 */
	<T> Optional<T> getOptionalGrain(Class<T> clazz);

	/**
	 * Gets a grain that is matching the name provided.
	 *
	 * @param name name of the grain.
	 * @return the grain.
	 * @param <T> type of the grain.
	 */
	<T> Optional<T> getOptionalGrain(String name);

	/**
	 * Gets all grains that are annotated with the provided annotation.
	 *
	 * @param clazz annotation to match.
	 * @return collection of grains.
	 * @param <T> type of the grain.
	 */
	<T> Collection<T> getGrains(Class<T> clazz);

	/**
	 * Checks if the container contains a grain matching the class provided or any of its inheritors or implementors.
	 * @param clazz class to match.
	 * @return true if the container contains the grain, false otherwise.
	 */
	boolean containsGrain(Class<?> clazz);
}
