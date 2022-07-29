package com._7aske.grain.logging;

public class LoggerFactory {
	public static Logger getLogger(Class<?> clazz) {
		return new ConsoleLogger(clazz.getName());
	}
}
