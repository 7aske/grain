package com._7aske.grain.core.component;

import com._7aske.grain.GrainAppRunner;
import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.util.By;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.util.*;

class DependencyContainerImpl implements DependencyContainer, Iterable<Injectable> {
	private final Collection<Injectable> dependencies;

	public DependencyContainerImpl() {
		this.dependencies = new PriorityQueue<>(By.order());
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
				.toList();
	}

	@Override
	public Collection<Object> getGrainsAnnotatedBy(Class<? extends Annotation> clazz) {
		return getListAnnotatedByClass(clazz)
				.stream()
				.map(Injectable::getInstance)
				.toList();
	}

	@Override
	public <T> Optional<T> getOptionalGrain(Class<T> clazz) {
		return getByClass(clazz)
				.map(Injectable::getInstance)
				.map(clazz::cast);
	}

	@Override
	public @NotNull Iterator<Injectable> iterator() {
		return getAll().iterator();
	}

	void add(Injectable dependency) {
		dependencies.add(dependency);
	}

	List<Injectable> getListByName(String name) {
		return getAll().stream()
				.filter(d -> Objects.equals(d.getName().orElse(null), name))
				.toList();
	}

	Optional<Injectable> getByName(String name) {
		List<Injectable> list = getListByName(name);

		if (list.isEmpty()) {
			return Optional.empty();
		}

		if (list.size() > 1) {
			return resolveSingleDependency(name, list);
		}

		return Optional.of(list.get(0));
	}

	<T> List<Injectable> getListByClass(Class<T> clazz) {
		return getAll().stream()
				.filter(d -> clazz.isAssignableFrom(d.getType()))
				.toList();
	}

	 List<Injectable> getListAnnotatedByClass(Class<? extends Annotation> clazz) {
		return getAll().stream()
				.filter(d -> ReflectionUtil.isAnnotationPresent(d.getType(), clazz))
				.toList();
	}

	<T> Optional<Injectable> getByClass(Class<T> clazz) {
		List<Injectable> list = getListByClass(clazz);

		if (list.isEmpty()) {
			return Optional.empty();
		}

		if (list.size() > 1) {
			return resolveSingleDependency(clazz.getName(), list);
		}

		return Optional.of(list.get(0));
	}

	public Optional<Injectable> getByAnnotation(Class<? extends Annotation> clazz) {
		return getListAnnotatedByClass(clazz)
				.stream()
				.findFirst();
	}

	private Optional<Injectable> resolveSingleDependency(String name, List<Injectable> list) {
		List<Injectable> userDefined = list.stream()
				.filter(d -> {
					String basePackage = GrainAppRunner.class.getPackageName() + ".";
					String depPackage = d.getParent() == null
							? d.getType().getPackageName()
							: d.getParent().getType().getPackageName();
					return !depPackage.startsWith(basePackage);
				})
				.sorted(Injectable::compareTo)
				.toList();

		if (userDefined.size() > 1) {
			throw new IllegalStateException("More than one dependency of type/name '" + name + "' found.");
		}

		if (userDefined.size() == 1)
			return userDefined.stream().findFirst();

		return list.stream()
				.min(Injectable::compareTo);
	}

	public Collection<Injectable> getAll() {
		return dependencies
				.stream()
				.sorted(By.<Injectable>order().thenComparing(Injectable::getType, By::packages))
				.toList();

	}
}
