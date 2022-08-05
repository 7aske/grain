package com._7aske.grain.core.component;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyContainer implements Iterable<BetterDependency> {
	private final List<BetterDependency> dependencies;

	public DependencyContainer() {
		this.dependencies = new ArrayList<>();
	}

	public void add(BetterDependency dependency) {
		dependencies.add(dependency);
	}

	public List<BetterDependency> getAll() {
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
			throw new IllegalStateException("More than one dependency with name '" + name + "'");
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
			throw new IllegalStateException("More than one dependency of type '" + clazz + "'");
		}

		return Optional.of(list.get(0));
	}

	@Override
	public Iterator<BetterDependency> iterator() {
		return dependencies.iterator();
	}
}
