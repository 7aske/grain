package com._7aske.grain.util.iterator;

import com._7aske.grain.component.Priority;

public class SortUtil {
	private SortUtil() {
	}

	public static int sortByPriority(Class<?> c1, Class<?> c2) {
		if (c1.isAnnotationPresent(Priority.class) && c2.isAnnotationPresent(Priority.class)) {
			Priority p1 = c1.getAnnotation(Priority.class);
			Priority p2 = c2.getAnnotation(Priority.class);
			return -Integer.compare(p1.value(), p2.value());
		}
		return 0;
	}

	public static int sortByPriority(Object o1, Object o2) {
		return sortByPriority(o1.getClass(), o2.getClass());
	}
}
