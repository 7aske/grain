package com._7aske.grain.config;

import static com._7aske.grain.constants.ServerConstants.PORT_MAX_VALUE;
import static com._7aske.grain.constants.ServerConstants.PORT_MIN_VALUE;

public class Configuration {
	private int port = 8080;
	private String host = "0.0.0.0";
	private int threads = 100;

	private Configuration(){}

	public static Configuration createDefault() {
		return new Configuration();
	}

	public Configuration(Configuration other) {
		this.port = other.port;
		this.host = other.host;
		this.threads = other.threads;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		if (threads < 1)
			throw new IllegalArgumentException("Thread count must not be less than 1");
		this.threads = threads;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (port < PORT_MIN_VALUE || port > PORT_MAX_VALUE)
			throw new IllegalArgumentException(String.format("Port must be between %d and %d", PORT_MIN_VALUE, PORT_MAX_VALUE));
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
