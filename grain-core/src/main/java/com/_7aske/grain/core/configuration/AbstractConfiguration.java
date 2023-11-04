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

	public AbstractConfiguration set(String prop, Object value) {
		properties.put(prop, value);
		return this;
	}

	public String get(String prop, String defaultValue) {
		Object obj = properties.getOrDefault(prop, defaultValue);
		if (obj instanceof String retval) {
			return retval;
		}

		return obj == null ? defaultValue : obj.toString();
	}


	public String get(String prop) {
		return get(prop, null);
	}

	public boolean getBoolean(String prop) {
		return Boolean.TRUE.equals(
				PropertiesHelper.getProperty(prop,
						properties,
						Boolean.class,
						Boolean::parseBoolean,
						false));
	}

	public boolean getBoolean(String prop, boolean defaultValue) {
		return Boolean.TRUE.equals(
				PropertiesHelper.getProperty(
						prop,
						properties,
						Boolean.class,
						Boolean::parseBoolean,
						defaultValue));
	}

	public Integer getInt(String prop) {
		return PropertiesHelper.getProperty(prop, properties, Integer.class, Integer::parseInt);
	}

	public Integer getInt(String prop, Integer defaultValue) {
		return PropertiesHelper.getProperty(prop, properties, Integer.class, Integer::parseInt, defaultValue);
	}

	public Long getLong(String prop) {
		return PropertiesHelper.getProperty(prop, properties, Long.class, Long::parseLong);
	}

	public Long getLong(String prop, Long defaultValue) {
		return PropertiesHelper.getProperty(prop, properties, Long.class, Long::parseLong, defaultValue);
	}

	public Float getFloat(String prop) {
		return PropertiesHelper.getProperty(prop, properties, Float.class, Float::parseFloat);
	}

	public Float getFloat(String prop, Float defaultValue) {
		return PropertiesHelper.getProperty(prop, properties, Float.class, Float::parseFloat, defaultValue);
	}

	public Double getDouble(String prop) {
		return PropertiesHelper.getProperty(prop, properties, Double.class, Double::parseDouble);
	}

	public Double getDouble(String prop, Double defaultValue) {
		return PropertiesHelper.getProperty(prop, properties, Double.class, Double::parseDouble, defaultValue);
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
