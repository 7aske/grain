package com._7aske.grain.core.configuration;

import com._7aske.grain.util.properties.PropertiesHelper;

import java.util.Properties;

public abstract class AbstractConfiguration {
	protected final Properties properties;

	protected AbstractConfiguration(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}

	public AbstractConfiguration set(ConfigurationKey prop, Object value) {
		return set(prop.getKey(), value);
	}

	public AbstractConfiguration set(String prop, Object value) {
		properties.put(prop, value);
		return this;
	}

	public String get(String prop, String defaultValue) {
		Object retval = properties.getOrDefault(prop, defaultValue);
		if (retval instanceof String) {
			return (String) retval;
		}

		return retval == null ? defaultValue : retval.toString();
	}

	public String get(ConfigurationKey prop, String defaultValue) {
		return get(prop.getKey(), defaultValue);
	}

	public String get(ConfigurationKey prop) {
		return get(prop.getKey(), null);
	}

	public String get(String prop) {
		return get(prop, null);
	}

	public boolean getBoolean(ConfigurationKey prop) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Boolean.class, Boolean::parseBoolean, false);
	}

	public boolean getBoolean(ConfigurationKey prop, boolean defaultValue) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Boolean.class, Boolean::parseBoolean, defaultValue);
	}

	public Integer getInt(ConfigurationKey prop) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Integer.class, Integer::parseInt);
	}

	public Integer getInt(ConfigurationKey prop, Integer defaultValue) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Integer.class, Integer::parseInt, defaultValue);
	}

	public Long getLong(ConfigurationKey prop) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Long.class, Long::parseLong);
	}

	public Long getLong(ConfigurationKey prop, Long defaultValue) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Long.class, Long::parseLong, defaultValue);
	}

	public Float getFloat(ConfigurationKey prop) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Float.class, Float::parseFloat);
	}

	public Float getFloat(ConfigurationKey prop, Float defaultValue) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Float.class, Float::parseFloat, defaultValue);
	}

	public Double getDouble(ConfigurationKey prop) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Double.class, Double::parseDouble);
	}

	public Double getDouble(ConfigurationKey prop, Double defaultValue) {
		return PropertiesHelper.getProperty(prop.getKey(), properties, Double.class, Double::parseDouble, defaultValue);
	}

	public Properties getPropertiesWithPrefix(String prefix) {
		Properties copy = new Properties();
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(prefix)) {
				copy.setProperty(key, properties.getProperty(key));
			}
		}
		return copy;
	}
}
