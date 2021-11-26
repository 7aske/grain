package com._7aske.grain.util.classloader;

import java.util.Set;
import java.util.function.Predicate;

public interface GrainClassLoader {
	Set<Class<?>> loadClasses(Predicate<Class<?>> predicate);
	Set<Class<?>> loadClasses();
}
