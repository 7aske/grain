package com._7aske.grain.core.context;

import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.DependencyContainer;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.GrainInjector;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.classloader.GrainJarClassLoader;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static com._7aske.grain.util.ReflectionUtil.isAnnotationPresent;

public class ApplicationContextImpl implements DependencyContainer, ApplicationContext {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextImpl.class);
	private final String basePackage;
	private final Configuration configuration;
	private final DependencyContainer dependencyContainer;

	public ApplicationContextImpl(String basePackage, Configuration configuration) {
		this.basePackage = basePackage;
		this.configuration = configuration;
		GrainInjector grainInitializer = new GrainInjector(configuration);

		Set<Class<?>> classes = Arrays.stream(new String[]{GrainApp.getBasePackage(), basePackage})
				.flatMap(pkg -> new GrainJarClassLoader(pkg)
						.loadClasses(cl -> !cl.isAnnotation() && isAnnotationPresent(cl, Grain.class))
						.stream())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		grainInitializer.inject(classes);

		this.dependencyContainer = grainInitializer.getContainer();
	}

	public ApplicationContextImpl(String basePackage) {
		this(basePackage, Configuration.createDefault());
	}

	@Override
	public DependencyContainer getDependencyContainer() {
		return dependencyContainer;
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
	public <T> Optional<T> getOptionalGrain(Class<T> clazz) {
		return dependencyContainer.getOptionalGrain(clazz);
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
