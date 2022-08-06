package com._7aske.grain.core.component;

import com._7aske.grain.GrainApp;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyContainer implements Iterable<BetterDependency> {
	private final Collection<BetterDependency> dependencies;

	public DependencyContainer() {
		// Using TreeSet to allow for dependency ordering by their own dependency
		// numbers. This will allow resolving more efficiently.
		this.dependencies = new PriorityQueue<>(Comparator.comparing(
				BetterDependency::getDependencies,
				Comparator.comparing(Collection::size))
		);
	}

	public void add(BetterDependency dependency) {
		dependencies.add(dependency);
	}

	public Collection<BetterDependency> getAll() {
		return dependencies;
	}

	public List<BetterDependency> getListByName(String name) {
		return dependencies.stream()
				.filter(d -> Objects.equals(d.getName().orElse(null), name))
				.collect(Collectors.toList());
	}

	public Optional<BetterDependency> getByName(String name) {
		List<BetterDependency> list = getListByName(name);

		if (list.isEmpty()) {
			return Optional.empty();
		}

		if (list.size() > 1) {
			List<BetterDependency> userDefined = list.stream()
					.filter(d -> {
						String basePackage = GrainApp.class.getPackage().getName() + ".";
						return !d.getType().getName().startsWith(basePackage);
					})
					.collect(Collectors.toList());
			if (userDefined.size() == 1) {
				return Optional.of(userDefined.get(0));
			}
			throw new IllegalStateException("More than one dependency with name '" + name + "' found. ");
		}

		return Optional.of(list.get(0));
	}

	public List<BetterDependency> getListByClass(Class<?> clazz) {
		return dependencies.stream()
				.filter(d -> clazz.isAssignableFrom(d.getType()))
				.collect(Collectors.toList());
	}

	public Optional<BetterDependency> getByClass(Class<?> clazz) {
		List<BetterDependency> list = getListByClass(clazz);

		if (list.isEmpty()) {
			return Optional.empty();
		}

		if (list.size() > 1) {
			List<BetterDependency> userDefined = list.stream()
					.filter(d -> {
						String basePackage = GrainApp.class.getPackage().getName() + ".";
						return !d.getType().getName().startsWith(basePackage);
					})
					.collect(Collectors.toList());
			if (userDefined.size() == 1) {
				return Optional.of(userDefined.get(0));
			}
			throw new IllegalStateException("More than one dependency of type '" + clazz + "' found.");
		}

		return Optional.of(list.get(0));
	}

	@Override
	public Iterator<BetterDependency> iterator() {
		return dependencies.iterator();
	}
}
