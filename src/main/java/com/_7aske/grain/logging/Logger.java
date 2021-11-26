package com._7aske.grain.logging;

import java.time.format.DateTimeFormatter;

public abstract class Logger {
	protected final static String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy hh:mm:ss.SSS";
	protected final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STRING);

	public abstract void trace(String s, Object... params);
	public abstract void info(String s, Object... params);
	public abstract void warn(String s, Object... params);
	public abstract void debug(String s, Object... params);
	public abstract void error(String s, Object... params);

	public final String name;

	protected Logger(String name) {
		this.name = name;
	}
}
