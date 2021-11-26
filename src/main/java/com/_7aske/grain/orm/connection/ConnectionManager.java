package com._7aske.grain.orm.connection;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.orm.exception.GrainDbConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com._7aske.grain.config.Configuration.Key.*;


@Grain
public final class ConnectionManager {
	@Inject
	public Configuration configuration;
	// @Temporary
	private boolean driverInitialized = false;
	private Connection connection;

	// Generates url from injected database properties
	public String getConnectionUrl() {
		// @formatter:off
		String host = (String) configuration.getProperty(DATABASE_HOST);
		int port    = (int)    configuration.getProperty(DATABASE_PORT);
		String name = (String) configuration.getProperty(DATABASE_NAME);
		String url  = (String) configuration.getProperty(DATABASE_URL);
		// @formatter:on
		if (url != null)
			return url;
		// Possibly @Incomplete. Handle all cases for different connection url
		// schemas.
		return String.format("jdbc:mysql://%s:%d/%s", host, port, name);
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				initializeConnection();
			}
			return connection;
		} catch (SQLException e) {
			throw new GrainDbConnectionException(e);
		}
	}

	public void initializeConnection() {
		if (!driverInitialized) {
			initializeDriver();
			driverInitialized = false;
		}
		String user = (String) configuration.getProperty(DATABASE_USER);
		String pass = (String) configuration.getProperty(DATABASE_PASS);
		try {
			connection = DriverManager.getConnection(getConnectionUrl(), user, pass);
		} catch (SQLException e) {
			throw new GrainDbConnectionException(e);
		}
	}

	private void initializeDriver() {
		try {
			String className = (String) configuration.getProperty(DATABASE_DRIVER_CLASS);
			if (className != null) {
				Class.forName(className);
			}
		} catch (ClassNotFoundException e) {
			throw new GrainDbConnectionException(e);
		}
	}
}
