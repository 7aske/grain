package com._7aske.grain.core.context;

import com._7aske.grain.GrainAppRunner;
import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.GrainInjector;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.GrainApplication;
import com._7aske.grain.core.configuration.GrainFertilizer;
import com._7aske.grain.core.reflect.classloader.GrainJarClassLoader;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.core.reflect.ReflectionUtil.isAnyAnnotationPresent;

public class ApplicationContextImpl implements ApplicationContext {
	private final String basePackage;
	private final Configuration configuration;
	private final DependencyContainer dependencyContainer;

	public ApplicationContextImpl(String basePackage, Configuration configuration) {
		this.basePackage = basePackage;
		this.configuration = configuration;
		GrainInjector grainInitializer = new GrainInjector(configuration);
		grainInitializer.inject(this);

		Set<Class<?>> classes = Arrays.stream(new String[]{GrainAppRunner.class.getPackageName(), basePackage})
				.flatMap(pkg -> new GrainJarClassLoader(pkg)
						.loadClasses(cl -> !cl.isAnnotation() && isAnyAnnotationPresent(cl, Grain.class, GrainFertilizer.class, GrainApplication.class))
						.stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		grainInitializer.inject(classes);

		this.dependencyContainer = grainInitializer.getContainer();
	}

	public ApplicationContextImpl(String basePackage) {
		this(basePackage, Configuration.createDefault());
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public String getPackage() {
		return basePackage;
	}

	@Override
	public <T> T getGrain(Class<T> clazz) {
		return dependencyContainer.getGrain(clazz);
	}

	@Override
	public <T> T getGrain(String name) {
		return dependencyContainer.getGrain(name);
	}

	@Override
	public void registerGrain(Object grain) {
		dependencyContainer.registerGrain(grain);
	}

	@Override
	public <T> Optional<T> getOptionalGrain(Class<T> clazz) {
		return dependencyContainer.getOptionalGrain(clazz);
	}

	@Override
	public <T> Optional<T> getOptionalGrain(String name) {
		return dependencyContainer.getOptionalGrain(name);
	}

	@Override
	public <T> Collection<T> getGrains(Class<T> clazz) {
		return dependencyContainer.getGrains(clazz);
	}

	@Override
	public Collection<Object> getGrainsAnnotatedBy(Class<? extends Annotation> clazz) {
		return dependencyContainer.getGrainsAnnotatedBy(clazz);
	}
}
