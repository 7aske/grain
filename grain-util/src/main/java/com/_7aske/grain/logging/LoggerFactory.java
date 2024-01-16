package com._7aske.grain.logging;

import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.LogManager;

/**
 * Class responsible for creating {@link java.util.logging.Logger} instances. Also,
 * this class does the ad-hoc configuration of LogManager.
 */
public class LoggerFactory {
	// Properties from logging.properties files.
	private static final Properties properties = new Properties();


	private LoggerFactory() {
	}

	public static Properties getProperties() {
		return properties;
	}

	/**
	 * Creates a new configured {@link Logger} instance.
	 *
	 * @param clazz Class to create a logger for.
	 * @return A new configured {@link Logger} instance.
	 */
	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	/**
	 * Creates a new configured {@link Logger} instance.
	 *
	 * @param name Name of the new logger.
	 * @return A new configured {@link Logger} instance.
	 */
	public static Logger getLogger(String name) {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
		LogManager logManager = LogManager.getLogManager();

		// We have to set the level manually as LogManager will not do that for us.
		setLevel(logger);

		// Just in case we try to fetch this exact logger again.
		logManager.addLogger(logger);

		return new LoggerDelegate(logger);
	}

	/**
	 * Utility method to set the logger level from saved properties.
	 *
	 * @param logger The logger to set the level for.
	 */
	private static void setLevel(java.util.logging.Logger logger) {
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String key = (String) propertyNames.nextElement();
			if (!key.endsWith(".level")) {
				// Not a level definition.
				continue;
			}

			// Length of ".level" string
			int ix = key.length() - 6;
			String name = key.substring(0, ix);
			if (name.isBlank() || logger.getName().startsWith(name)) {
				com._7aske.grain.logging.Level.findLevel(properties.getProperty(key))
						.ifPresent(logger::setLevel);
			}
		}
	}
}
