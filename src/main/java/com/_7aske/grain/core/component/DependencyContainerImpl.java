package com._7aske.grain.core.component;

import com._7aske.grain.GrainApp;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

class DependencyContainerImpl implements DependencyContainer, Iterable<Injectable<?>> {
	private final Collection<Injectable<?>> dependencies;

	public DependencyContainerImpl() {
		// Using TreeSet to allow for dependency ordering by their own dependency
		// numbers. This will allow resolving more efficiently.
		this.dependencies = new PriorityQueue<>(Comparator.comparing(
				Injectable::getDependencies,
				Comparator.comparing(Collection::size))
		);
	}

	void add(Injectable<?> dependency) {
		dependencies.add(dependency);
	}

	List<Injectable<?>> getListByName(String name) {
		return dependencies.stream()
				.filter(d -> Objects.equals(d.getName().orElse(null), name))
				.sorted(Comparator.comparing(Injectable::getOrder))
				.collect(Collectors.toList());
	}

	Optional<Injectable<?>> getByName(String name) {
		List<Injectable<?>> list = getListByName(name);

		if (list.isEmpty()) {
			return Optional.empty();
		}

		if (list.size() > 1) {
			return resolveSingleDependency(name, list);
		}

		return Optional.of(list.get(0));
	}

	<T> List<Injectable<?>> getListByClass(Class<T> clazz) {
		return dependencies.stream()
				.filter(d -> clazz.isAssignableFrom(d.getType()))
				.sorted(Comparator.comparing(Injectable::getOrder))
				.collect(Collectors.toList());
	}

	List<Injectable<?>> getListAnnotatedByClass(Class<? extends Annotation> clazz) {
		return dependencies.stream()
				.filter(d -> ReflectionUtil.isAnnotationPresent(d.getType(), clazz))
				.sorted(Comparator.comparing(Injectable::getOrder))
				.collect(Collectors.toList());
	}

	<T> Optional<Injectable<?>> getByClass(Class<T> clazz) {
		List<Injectable<?>> list = getListByClass(clazz);

		if (list.isEmpty()) {
			return Optional.empty();
		}

		if (list.size() > 1) {
			return resolveSingleDependency(clazz.getName(), list);
		}

		return Optional.of(list.get(0));
	}

	@Override
	public Iterator<Injectable<?>> iterator() {
		return dependencies.iterator();
	}

	private Optional<Injectable<?>> resolveSingleDependency(String name, List<Injectable<?>> list)  {
		List<Injectable<?>> userDefined = list.stream()
				.filter(d -> {
					String basePackage = GrainApp.getBasePackage() + ".";
					String depPackage = d.getProvider() == null
							? d.getType().getPackageName()
							: d.getProvider().getType().getPackageName();
					return !depPackage.startsWith(basePackage);
				})
				.collect(Collectors.toList());

		if (userDefined.size() > 1) {
			throw new IllegalStateException("More than one dependency of type/name '" + name + "' found.");
		}

		if (userDefined.size() == 1)
			return Optional.of(userDefined.get(0));

		// Should be sorted by @Order
		return list.stream().findFirst();
	}

	@Override
	public void registerGrain(Object grain) {
		this.dependencies.add(Injectable.ofInitialized(grain));
	}

	@Override
	public <T> T getGrain(Class<T> clazz) {
		return clazz.cast(getByClass(clazz)
				.map(Injectable::getInstance)
				.orElseThrow(() -> new IllegalStateException("No dependency of type '" + clazz.getName() + "' found.")));
	}

	@Override
	public <T> Collection<T> getGrains(Class<T> clazz) {
		return getListByClass(clazz)
				.stream()
				.map(Injectable::getInstance)
				.map(clazz::cast)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<Object> getGrainsAnnotatedBy(Class<? extends Annotation> clazz) {
		return getListAnnotatedByClass(clazz)
				.stream()
				.map(Injectable::getInstance)
				.collect(Collectors.toList());
	}

	@Override
	public <T> Optional<T> getOptionalGrain(Class<T> clazz) {
		return getByClass(clazz)
				.map(Injectable::getInstance)
				.map(clazz::cast);
	}

	public Collection<Injectable<?>> getAll() {
		return dependencies;
	}
}
