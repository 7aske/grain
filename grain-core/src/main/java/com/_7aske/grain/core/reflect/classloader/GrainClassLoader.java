package com._7aske.grain.core.reflect.classloader;

import java.util.Set;
import java.util.function.Predicate;

public interface GrainClassLoader {
	Set<Class<?>> loadClasses(Predicate<Class<?>> predicate);
	Set<Class<?>> loadClasses();
}
