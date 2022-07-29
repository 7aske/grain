package com._7aske.grain.config;

public enum ConfigurationKey {
	// @formatter:off
	SECURITY_ENABLED             ("security.enabled"),
	SESSION_ENABLED              ("session.enabled"),
	SESSION_MAX_AGE              ("session.max-age"),
	SERVER_PORT                  ("server.port"),
	SERVER_HOST                  ("server.host"),
	SERVER_THREADS               ("server.threads"),
	DATABASE_HOST                ("database.host"),
	DATABASE_NAME                ("database.name"),
	DATABASE_PORT                ("database.port"),
	DATABASE_USER                ("database.user"),
	DATABASE_PASS                ("database.pass"),
	DATABASE_URL                 ("database.url"),
	DATABASE_DRIVER_CLASS        ("database.driver_class"),
	DATABASE_EXECUTOR_PRINT_SQL  ("database.executor.print-sql"),
	REQUEST_HANDLER_ACCESS_LOG   ("request-handler.access-log"),
	LOG_LEVEL                    ("logging.level"),
	DATABASE_POOL_SIZE           ("database.pool.size"),
	DATABASE_POOL_CONNECTION_WAIT("database.pool.connection-wait");
	// @formatter:on

	private final String key;

	ConfigurationKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
