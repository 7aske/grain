package com._7aske.grain.config;

public class ConfigurationBuilder {
	private Configuration instance = null;

	public ConfigurationBuilder() {
		this.instance = Configuration.createDefault();
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
