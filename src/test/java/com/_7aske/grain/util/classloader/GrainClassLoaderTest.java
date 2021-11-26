package com._7aske.grain.util.classloader;

import com._7aske.grain.GrainApp;
import org.junit.jupiter.api.Test;

import java.util.Set;

class GrainClassLoaderTest {

	@Test
	void test_classJarLoader() {
		GrainClassLoader classLoader = new GrainJarClassLoader(GrainApp.class.getPackageName());
		Set<Class<?>> set = classLoader.loadClasses();
		set.forEach(System.out::println);
	}

	@Test
	void test_classLoader() {
		GrainClassLoader classLoader = new GrainBasicClassLoader(GrainApp.class.getPackageName());
		Set<Class<?>> set = classLoader.loadClasses();
		set.forEach(System.out::println);
	}
}