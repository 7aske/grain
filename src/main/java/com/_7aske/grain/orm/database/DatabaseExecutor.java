package com._7aske.grain.orm.database;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.orm.connection.ConnectionManager;
import com._7aske.grain.orm.exception.GrainDbStatementException;

import java.sql.*;
import java.util.*;

import static com._7aske.grain.config.Configuration.Key.DATABASE_EXECUTOR_PRINT_SQL;

@Grain
public class DatabaseExecutor {
	@Inject
	protected ConnectionManager connectionManager;
	@Inject
	protected Configuration configuration;

	// Used for update and insert operations
	public long executeUpdate(String query) {
		if (Objects.equals(configuration.getProperty(DATABASE_EXECUTOR_PRINT_SQL), true)) {
			// @Temporary replace with logger
			System.err.println(query);
		}
		try (Connection connection = connectionManager.getConnection(); Statement statement = connection.createStatement()) {
			statement.executeUpdate(query);
			ResultSet resultSet = statement.getGeneratedKeys();
			// If there is a result we return the ID of the newly created row
			if (resultSet.next()) {
				// @Incomplete should probably handle the case where there are multiple
				// generated columns
				return resultSet.getLong(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			throw new GrainDbStatementException(e);
		}
	}

	public List<Map<String, String>> executeQuery(String query) {
		if (Objects.equals(configuration.getProperty(DATABASE_EXECUTOR_PRINT_SQL), true)) {
			// @Temporary replace with logger
			System.err.println(query);
		}
		List<Map<String, String>> out = new ArrayList<>();
		try (Connection connection = connectionManager.getConnection(); Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			ResultSetMetaData metaData = resultSet.getMetaData();
			while (resultSet.next()) {
				Map<String, String> data = new HashMap<>();
				int colCount = metaData.getColumnCount();
				for (int i = 1; i <= colCount; i++) {
					// @Note use getColumnLabel instead of getColumnName
					// because we're using aliases for referencing joined
					// column names.
					data.put(metaData.getColumnLabel(i), resultSet.getString(i));
				}
				out.add(data);
			}
		} catch (SQLException e) {
			throw new GrainDbStatementException(e);
		}
		return out;
	}
}
