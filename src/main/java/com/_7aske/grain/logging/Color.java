package com._7aske.grain.logging;

/**
 * Utility class for coloring text to be output on the terminal.
 */
public final class Color {
	private Color() {
	}

	public static final String BLACK = "\u001B[30m";
	public static final String BLACK_BACKGROUND = "\u001B[40m";
	public static final String RED = "\u001B[31m";
	public static final String RED_BACKGROUND = "\u001B[41m";
	public static final String GREEN = "\u001B[32m";
	public static final String GREEN_BACKGROUND = "\u001B[42m";
	public static final String YELLOW = "\u001B[33m";
	public static final String YELLOW_BACKGROUND = "\u001B[43m";
	public static final String BLUE = "\u001B[34m";
	public static final String BLUE_BACKGROUND = "\u001B[44m";
	public static final String PURPLE = "\u001B[35m";
	public static final String PURPLE_BACKGROUND = "\u001B[45m";
	public static final String CYAN = "\u001B[36m";
	public static final String CYAN_BACKGROUND = "\u001B[46m";
	public static final String WHITE = "\u001B[37m";
	public static final String WHITE_BACKGROUND = "\u001B[47m";
	public static final String RESET = "\u001B[0m";

	public static String red(String text) {
		return RED + text + RESET;
	}

	public static String black(String text) {
		return BLACK + text + RESET;
	}

	public static String green(String text) {
		return GREEN + text + RESET;
	}

	public static String yellow(String text) {
		return YELLOW + text + RESET;
	}

	public static String blue(String text) {
		return BLUE + text + RESET;
	}

	public static String purple(String text) {
		return PURPLE + text + RESET;
	}

	public static String cyan(String text) {
		return CYAN + text + RESET;
	}

	public static String white(String text) {
		return WHITE + text + RESET;
	}
}
