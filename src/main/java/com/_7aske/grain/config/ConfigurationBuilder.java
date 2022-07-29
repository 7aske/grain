package com._7aske.grain.config;

public class ConfigurationBuilder {
	private Configuration instance = null;

	public ConfigurationBuilder() {
		this.instance = Configuration.createDefault();
	}

	// Sets the property directly to Configuration.properties
	public void setProperty(ConfigurationKey prop, Object value) {
		this.instance.setProperty(prop, value);
	}

	// Allows to set any arbitrary key to Configuration.properties
	public void setPropertyUnsafe(String prop, Object value) {
		this.instance.setPropertyUnsafe(prop, value);
	}

	public ConfigurationBuilder port(int port) {
		this.instance.setPort(port);
		return this;
	}

	public ConfigurationBuilder host(String host) {
		this.instance.setHost(host);
		return this;
	}

	public ConfigurationBuilder threads(int threads) {
		this.instance.setThreads(threads);
		return this;
	}


	public Configuration build() {
		return instance;
	}
}
