package com._7aske.grain.core.configuration;

public final class ConfigurationKey {

	private ConfigurationKey() {
	}

	// @formatter:off
	public static final String PROFILES_ACTIVE              = "grain.profiles.active";
	public static final String SECURITY_ENABLED             = "grain.security.enabled";
	public static final String SESSION_ENABLED              = "grain.session.enabled";
	public static final String SESSION_MAX_AGE              = "grain.session.max-age";
	public static final String SERVER_CONTEXT_PATH          = "grain.server.context-path";
	public static final String SERVER_PORT                  = "grain.server.port";
	public static final String SERVER_HOST                  = "grain.server.host";
	public static final String SERVER_THREADS               = "grain.server.threads";
	public static final String DATABASE_HOST                = "grain.database.host";
	public static final String DATABASE_NAME                = "grain.database.name";
	public static final String DATABASE_PORT                = "grain.database.port";
	public static final String DATABASE_USER                = "grain.database.user";
	public static final String DATABASE_PASS                = "grain.database.pass";
	public static final String DATABASE_URL                 = "grain.database.url";
	public static final String DATABASE_DRIVER_CLASS        = "grain.database.driver_class";
	public static final String DATABASE_EXECUTOR_PRINT_SQL  = "grain.database.executor.print-sql";
	public static final String REQUEST_HANDLER_ACCESS_LOG   = "grain.request-handler.access-log";
	public static final String DATABASE_POOL_SIZE           = "grain.database.pool.size";
	public static final String DATABASE_POOL_CONNECTION_WAIT= "grain.database.pool.connection-wait";
	public static final String CACHE_ENABLED                = "grain.cache.enabled";
	// @formatter:on
}
