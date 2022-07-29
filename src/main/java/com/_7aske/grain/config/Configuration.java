package com._7aske.grain.config;

import com._7aske.grain.http.session.SessionConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.LogManager;

import static com._7aske.grain.config.ConfigurationKey.*;
import static com._7aske.grain.constants.ServerConstants.PORT_MAX_VALUE;
import static com._7aske.grain.constants.ServerConstants.PORT_MIN_VALUE;

// @Warning we manually add this class in the dependency injection pipeline
// do not mark it with @Grain
public final class Configuration {
	private final Properties properties;

	private Configuration() {
		properties = new Properties();
		setProperty(SERVER_HOST, "0.0.0.0");
		setProperty(SERVER_PORT, 8080);
		setProperty(SERVER_THREADS, 100);
		setProperty(REQUEST_HANDLER_ACCESS_LOG, true);
		setProperty(DATABASE_EXECUTOR_PRINT_SQL, true);
		setProperty(DATABASE_POOL_SIZE, true);
		setProperty(SESSION_ENABLED, true);
		setProperty(SESSION_MAX_AGE, SessionConstants.SESSION_DEFAULT_MAX_AGE);
		setProperty(SECURITY_ENABLED, false);
		setProperty(LOG_LEVEL, "info");

		// @Temporary
		ClassLoader classLoader = Configuration.class.getClassLoader();
		try {
			Enumeration<URL> urls = classLoader.getResources("application.properties");
			urls.asIterator().forEachRemaining(url -> {
				try (InputStream inputStream = url.openStream()) {
					properties.load(inputStream);
				} catch (IOException ignored) {/*ignored*/}
			});
		} catch (IOException ignored) {/*ignored*/}

		try {
			Enumeration<URL> urls = classLoader.getResources("logging.properties");
			urls.asIterator().forEachRemaining(url -> {
				try (InputStream inputStream = url.openStream()) {
					LogManager.getLogManager().readConfiguration(inputStream);
				} catch (IOException ignored) {/*ignored*/}
			});
		} catch (IOException ignored) {/*ignored*/}

		properties.forEach((key, value) -> {
			System.setProperty(key.toString(), value.toString());
		});
	}

	public static Configuration createDefault() {
		return new Configuration();
	}

	public void setProperty(ConfigurationKey prop, Object value) {
		properties.put(prop.getKey(), value);
	}

	// Allows to set any arbitrary key
	public void setPropertyUnsafe(String prop, Object value) {
		properties.put(prop, value);
	}

	public Object getProperty(ConfigurationKey prop) {
		return properties.get(prop.getKey());
	}

	public <T> T getProperty(ConfigurationKey prop, T _default) {
		return (T) properties.getOrDefault(prop, _default);
	}

	// Allows to get any arbitrary key
	public Object getPropertyUnsafe(String prop) {
		return properties.get(prop);
	}

	public Properties getProperties() {
		return properties;
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
		return Integer.parseInt(getProperty(SERVER_PORT).toString());
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

}
