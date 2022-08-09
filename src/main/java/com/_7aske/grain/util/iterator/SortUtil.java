package com._7aske.grain.util.iterator;

import com._7aske.grain.core.component.Order;

public class SortUtil {
	private SortUtil() {
	}

	public static int sortByPriority(Class<?> c1, Class<?> c2) {
		if (c1.isAnnotationPresent(Order.class) && c2.isAnnotationPresent(Order.class)) {
			Order p1 = c1.getAnnotation(Order.class);
			Order p2 = c2.getAnnotation(Order.class);
			return -Integer.compare(p1.value(), p2.value());
		}
		return 0;
	}

	public static int sortByPriority(Object o1, Object o2) {
		return sortByPriority(o1.getClass(), o2.getClass());
	}
}
