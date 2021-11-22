package com._7aske.grain.orm.database;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.orm.connection.ConnectionManager;
import com._7aske.grain.orm.exception.GrainDbStatementException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Grain
public class DatabaseExecutor {
	@Inject
	public ConnectionManager connectionManager;

	public long executeUpdate(String query) {
		Connection connection = connectionManager.getConnection();
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(query);
			// @Incomplete
			return -1;
		} catch (SQLException e) {
			throw new GrainDbStatementException(e);
		}
	}

	public ResultSet executeQuery(String query) {
		Connection connection = connectionManager.getConnection();
		try (Statement statement = connection.createStatement()) {
			return statement.executeQuery(query);
		} catch (SQLException e) {
			throw new GrainDbStatementException(e);
		}
	}
}
