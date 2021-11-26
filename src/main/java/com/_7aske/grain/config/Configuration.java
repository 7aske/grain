package com._7aske.grain.config;

import java.util.Properties;

import static com._7aske.grain.config.Configuration.Key.*;
import static com._7aske.grain.constants.ServerConstants.PORT_MAX_VALUE;
import static com._7aske.grain.constants.ServerConstants.PORT_MIN_VALUE;

// @Warning we manually add this class in the dependency injection pipeline
// do not mark it with @Grain
public class Configuration {
	private final Properties properties;

	private Configuration(){
		properties = new Properties();
		setProperty(SERVER_HOST, "0.0.0.0");
		setProperty(SERVER_PORT, 8080);
		setProperty(SERVER_THREADS, 100);
		setProperty(REQUEST_HANDLER_ACCESS_LOG, true);
		setProperty(DATABASE_EXECUTOR_PRINT_SQL, true);
		setProperty(SESSION_ENABLED, true);
	}

	public static Configuration createDefault() {
		return new Configuration();
	}

	public void setProperty(Configuration.Key prop, Object value) {
		properties.put(prop.getKey(), value);
	}

	// Allows to set any arbitrary key
	public void setPropertyUnsafe(String prop, Object value) {
		properties.put(prop, value);
	}

	public Object getProperty(Configuration.Key prop) {
		return properties.get(prop.getKey());
	}

	// Allows to get any arbitrary key
	public Object getPropertyUnsafe(String prop) {
		return properties.get(prop);
	}

	public int getThreads() {
		return (int) getProperty(SERVER_THREADS);
	}

	public void setThreads(int threads) {
		if (threads < 1)
			throw new IllegalArgumentException("Thread count must not be less than 1");
		this.setProperty(SERVER_THREADS, threads);
	}

	public int getPort() {
		return (int) getProperty(SERVER_PORT);
	}

	public void setPort(int port) {
		if (port < PORT_MIN_VALUE || port > PORT_MAX_VALUE)
			throw new IllegalArgumentException(String.format("Port must be between %d and %d", PORT_MIN_VALUE, PORT_MAX_VALUE));
		this.setProperty(SERVER_PORT, port);
	}

	public String getHost() {
		return (String) getProperty(SERVER_HOST);
	}

	public void setHost(String host) {
		this.setProperty(SERVER_HOST, host);
	}

	public enum Key {
		// @formatter:off
	    SESSION_ENABLED             ("session.enabled"),
		SERVER_PORT                 ("server.port"),
		SERVER_HOST                 ("server.host"),
		SERVER_THREADS              ("server.threads"),
		DATABASE_HOST               ("database.host"),
		DATABASE_NAME               ("database.name"),
		DATABASE_PORT               ("database.port"),
		DATABASE_USER               ("database.user"),
		DATABASE_PASS               ("database.pass"),
		DATABASE_URL                ("database.url"),
		DATABASE_DRIVER_CLASS       ("database.driver_class"),
		DATABASE_EXECUTOR_PRINT_SQL ("database.executor.print-sql"),
		REQUEST_HANDLER_ACCESS_LOG  ("request-handler.access-log");
		// @formatter:on

		private final String key;

		Key(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
