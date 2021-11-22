package com._7aske.grain.orm.connection;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.orm.exception.GrainDbConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com._7aske.grain.config.ConfigurationKey.*;

@Grain
public final class ConnectionManager {
	@Inject
	public Configuration configuration;

	// Generates url from injected database properties
	public String getConnectionUrl() {
		// @formatter:off
		String host = (String) configuration.getProperties().get(DATABASE_HOST);
		int port    = (int)    configuration.getProperties().get(DATABASE_PORT);
		String name = (String) configuration.getProperties().get(DATABASE_NAME);
		String url  = (String) configuration.getProperties().get(DATABASE_URL);
		// @formatter:on
		if (url != null)
			return url;
		// Possibly @Incomplete. Handle all cases for different connection url
		// schemas.
		return String.format("mysql://%s:%d/%s", host, port, name);
	}

	public Connection getConnection() {
		String user = (String) configuration.getProperties().get(DATABASE_USER);
		String pass = (String) configuration.getProperties().get(DATABASE_PASS);
		try {
			return DriverManager.getConnection(getConnectionUrl(), user, pass);
		} catch (SQLException e) {
			throw new GrainDbConnectionException(e);
		}
	}
}
