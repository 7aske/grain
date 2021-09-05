package com._7aske.grain.util;

import java.lang.reflect.Array;

public class ArrayUtil {
	private ArrayUtil(){}

	public static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
	public static <T> T[] join(T[]... arrays) {
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}

		//T[] result = new T[length];
		final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

		int offset = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}

		return result;
	}
}
