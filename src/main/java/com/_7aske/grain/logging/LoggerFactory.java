package com._7aske.grain.logging;

import com._7aske.grain.core.configuration.PropertiesResolver;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;

import static java.util.logging.LogManager.getLogManager;

/**
 * Class responsible for creating {@link java.util.logging.Logger} instances. Also,
 * this class does the ad-hoc configuration of LogManager.
 */
public class LoggerFactory {
	private static final PropertiesResolver PROPERTIES_RESOLVER;
	// Properties from logging.properties files.
	private static final Properties properties = new Properties();
	static {
		// Profile-less configuration of LogManager.
		PROPERTIES_RESOLVER = new PropertiesResolver(List.of());
		PROPERTIES_RESOLVER.resolve("logging", is -> {
			byte[] bytes = is.readAllBytes();
			// We create a copy of read bytes since we need to use the input stream
			// twice. Once for properties  and once for the LogManager itself.
			// InputStream returned from PropertiesResolver#resolve() cannot be
			// reset to be read from again.
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
				properties.load(bais);
				bais.reset();
				getLogManager().readConfiguration(bais);
			}
		});
	}

	private LoggerFactory() {
	}

	/**
	 * Creates a new configured {@link Logger} instance.
	 *
	 * @param clazz Class that will be the name of the new logger.
	 * @return A new configured {@link Logger} instance.
	 */
	public static Logger getLogger(Class<?> clazz) {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(clazz.getName());
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
