package com._7aske.grain.config;

import com._7aske.grain.component.Grain;

import java.util.Properties;

import static com._7aske.grain.config.Configuration.Key.*;
import static com._7aske.grain.constants.ServerConstants.PORT_MAX_VALUE;
import static com._7aske.grain.constants.ServerConstants.PORT_MIN_VALUE;

@Grain
public class Configuration {
	private Properties properties;

	private Configuration(){
		properties = new Properties();
		properties.put(SERVER_HOST, "0.0.0.0");
		properties.put(SERVER_PORT, 8080);
		properties.put(SERVER_THREADS, 100);
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
		return (int) properties.get(SERVER_THREADS);
	}

	public void setThreads(int threads) {
		if (threads < 1)
			throw new IllegalArgumentException("Thread count must not be less than 1");
		this.properties.put(SERVER_THREADS, threads);
	}

	public int getPort() {
		return (int) properties.get(SERVER_PORT);
	}

	public void setPort(int port) {
		if (port < PORT_MIN_VALUE || port > PORT_MAX_VALUE)
			throw new IllegalArgumentException(String.format("Port must be between %d and %d", PORT_MIN_VALUE, PORT_MAX_VALUE));
		this.properties.put(SERVER_PORT, port);
	}

	public String getHost() {
		return (String) properties.get(SERVER_HOST);
	}

	public void setHost(String host) {
		this.properties.put(SERVER_HOST, host);
	}

	public enum Key {
		// @formatter:off
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
		DATABASE_EXECUTOR_PRINT_SQL ("database.executor.print-sql");
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
