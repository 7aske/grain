package com._7aske.grain.logging;

import java.util.List;
import java.util.Optional;

/**
 * Customized logging levels to be used with {@link java.util.logging.Logger}.
 */
public class Level extends java.util.logging.Level {
	private static final String DEFAULT_BUNDLE =
			"sun.util.logging.resources.logging";

	// Additional levels for logging.
	public static final java.util.logging.Level TRACE = new Level("TRACE", java.util.logging.Level.FINEST.intValue(), DEFAULT_BUNDLE);
	public static final java.util.logging.Level DEBUG = new Level("DEBUG", java.util.logging.Level.CONFIG.intValue(), DEFAULT_BUNDLE);
	public static final java.util.logging.Level WARN = new Level("WARN", java.util.logging.Level.WARNING.intValue(), DEFAULT_BUNDLE);
	public static final java.util.logging.Level ERROR = new Level("ERROR", java.util.logging.Level.SEVERE.intValue(), DEFAULT_BUNDLE);

	static List<java.util.logging.Level> ADDITIONAL_LEVELS = List.of(
			TRACE,
			DEBUG,
			ERROR
	);

	/**
	 * Finds the level with the given name. Takes into the account existing
	 * levels from java.util.logging.Level and custom ones defined in this
	 * class.
	 *
	 * @param val The name of the level to find.
	 * @return The level with the given name.
	 */
	public static Optional<java.util.logging.Level> findLevel(String val) {
		try {
			return Optional.of(java.util.logging.Level.parse(val));
		} catch (IllegalArgumentException ignored) {/*ignored*/}

		return ADDITIONAL_LEVELS.stream().filter(level -> level.getName().equals(val)).findFirst();
	}

	protected Level(String name, int value) {
		super(name, value);
	}

	public Level(String name, int value, String resourceBundleName) {
		super(name, value, resourceBundleName);
	}
}
