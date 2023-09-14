package com._7aske.grain.orm.connection;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionWrapper implements AutoCloseable {
	private Connection connection = null;
	private boolean working = false;

	public ConnectionWrapper() {
	}

	public boolean isActive() {
		if (connection == null) return false;
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			try {
				connection.close();
			} catch (SQLException ignored) {
				// ignored
			}
			return false;
		}
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public Connection get() {
		return this.connection;
	}

	public void restart(ConnectionManager connectionManager) {
		this.connection = connectionManager.initializeConnection();
	}

	@Override
	public void close() {
		this.working = false;
		if (connection != null) {
			try {
				if (connection.isClosed()) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ignored) {
				connection = null;
			}
		}
	}
}
