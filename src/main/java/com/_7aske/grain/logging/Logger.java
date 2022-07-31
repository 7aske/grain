package com._7aske.grain.logging;

/**
 * Logger interface that allows for standardized logging. Essentially a wrapper
 * around {@link java.util.logging.Logger} that allows us to use methods such as
 * {@link #debug(String, Object...)} and be more concise.
 */
public interface Logger {
	void log(java.util.logging.Level level, String s, Object... params);

	/**
	 * Logs a message at the TRACE level.
	 *
	 * @param s      The message to log.
	 * @param params The parameters to use when formatting the message.
	 */
	default void trace(String s, Object... params) {
		log(com._7aske.grain.logging.Level.TRACE, s, params);
	}

	/**
	 * Logs a message at the INFO level.
	 *
	 * @param s      The message to log.
	 * @param params The parameters to use when formatting the message.
	 */
	default void info(String s, Object... params) {
		log(java.util.logging.Level.INFO, s, params);
	}

	/**
	 * Logs a message at the WARNING level.
	 *
	 * @param s      The message to log.
	 * @param params The parameters to use when formatting the message.
	 */
	default void warn(String s, Object... params) {
		log(java.util.logging.Level.WARNING, s, params);
	}

	/**
	 * Logs a message at the DEBUG level.
	 *
	 * @param s      The message to log.
	 * @param params The parameters to use when formatting the message.
	 */
	default void debug(String s, Object... params) {
		log(Level.DEBUG, s, params);
	}

	/**
	 * Logs a message at the ERROR level.
	 *
	 * @param s      The message to log.
	 * @param params The parameters to use when formatting the message.
	 */
	default void error(String s, Object... params) {
		log(com._7aske.grain.logging.Level.ERROR, s, params);
	}

}
