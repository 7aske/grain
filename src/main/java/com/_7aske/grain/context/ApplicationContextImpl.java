package com._7aske.grain.context;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.config.GrainApplication;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;
import com._7aske.grain.util.classloader.GrainClassLoader;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext {
	private final GrainRegistry grainRegistry;
	private final StaticLocationsRegistry staticLocationsRegistry;
	private final String basePackage;

	public ApplicationContextImpl(String basePackage) {
		this.basePackage = basePackage;
		GrainClassLoader classLoader = new GrainClassLoader(this.basePackage);
		Set<Class<?>> classes = classLoader.loadClasses(c -> c.isAnnotationPresent(GrainApplication.class));
		List<String> packages = classes.stream().map(Class::getPackageName).collect(Collectors.toList());

		if (basePackage != null)
			packages.add(basePackage);

		this.grainRegistry = new GrainRegistry(packages.toArray(new String[0]));
		this.staticLocationsRegistry = new StaticLocationsRegistry();
	}

	@Override
	public GrainRegistry getGrainRegistry() {
		return grainRegistry;
	}

	@Override
	public StaticLocationsRegistry getStaticLocationsRegistry() {
		return staticLocationsRegistry;
	}

	@Override
	public String getPackage() {
		return basePackage;
	}
}
