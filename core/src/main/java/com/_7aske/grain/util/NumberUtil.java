package com._7aske.grain.util;

public class NumberUtil {
	public static Number getNumberOrFloat(String value) {
		try {
			float parsed = Float.parseFloat(value);
			if (parsed == (int) parsed) {
				return (int) parsed;
			} else {
				return parsed;
			}
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	public static boolean isNumberOrFloat(String value) {
		return getNumberOrFloat(value) != null;
	}
}
