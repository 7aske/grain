package com._7aske.grain.util;

public class ArrayUtil {
	private ArrayUtil(){}

	public static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
}
