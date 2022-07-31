package com._7aske.grain.logging;

import com._7aske.grain.annotation.NotNull;

import java.util.logging.Level;

/**
 * Delegate class that forwards all class to a {@link java.util.logging.Logger}
 * instance.
 */
public class LoggerDelegate implements Logger {
	private final java.util.logging.Logger logger;

	public LoggerDelegate(@NotNull java.util.logging.Logger logger) {
		this.logger = logger;
	}

	@Override
	public void log(Level level, String s, Object... params) {
		this.logger.log(level, s, params);
	}
}
