package com._7aske.grain.logging;

public abstract class Logger {
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
