package com._7aske.grain.orm.connection;

import com._7aske.grain.component.Condition;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.orm.exception.GrainDbConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com._7aske.grain.config.ConfigurationKey.*;


@Grain
@Condition("grain.persistence.provider == 'native'")
public final class ConnectionManager {
	@Inject
	public Configuration configuration;
	// @Temporary
	private boolean driverInitialized = false;

	// Generates url from injected database properties
	public String getConnectionUrl() {
		// @formatter:off
		String host = configuration.get(DATABASE_HOST);
		int port    = configuration.getInt(DATABASE_PORT);
		String name = configuration.get(DATABASE_NAME);
		String url  = configuration.get(DATABASE_URL);
		// @formatter:on
		if (url != null)
			return url;
		// Possibly @Incomplete. Handle all cases for different connection url
		// schemas.
		return String.format("jdbc:mysql://%s:%d/%s", host, port, name);
	}


	public Connection initializeConnection() {
		if (!driverInitialized) {
			initializeDriver();
			driverInitialized = false;
		}

		String user = configuration.get(DATABASE_USER);
		String pass = configuration.get(DATABASE_PASS);

		try {
			return DriverManager.getConnection(getConnectionUrl(), user, pass);
		} catch (SQLException e) {
			throw new GrainDbConnectionException(e);
		}
	}

	private void initializeDriver() {
		try {
			String className = configuration.get(DATABASE_DRIVER_CLASS);
			if (className != null) {
				Class.forName(className);
			}
		} catch (ClassNotFoundException e) {
			throw new GrainDbConnectionException(e);
		}
	}
}
