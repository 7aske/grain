package com._7aske.grain.logging;

public enum Level {
	OFF("OFF", 2147483647),
	TRACE("TRACE", 100),
	INFO("INFO", 300),
	WARN("WARN", 500),
	DEBUG("DEBUG", 700),
	ERROR("ERROR", 1000),
	ALL("ALL", -2147483648);

	private final String name;
	private final int value;

	Level(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}
