package com._7aske.grain.orm.database;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.orm.connection.ConnectionManager;
import com._7aske.grain.orm.exception.GrainDbStatementException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Grain
public class DatabaseExecutor {
	@Inject
	public ConnectionManager connectionManager;

	// Used for update and insert operations
	public long executeUpdate(String query) {
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

	public List<Map<String, Object>> executeQuery(String query) {
		List<Map<String, Object>> out = new ArrayList<>();
		try (Connection connection = connectionManager.getConnection(); Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			ResultSetMetaData metaData = resultSet.getMetaData();
			while (resultSet.next()) {
				Map<String, Object> data = new HashMap<>();
				int colCount = metaData.getColumnCount();
				for (int i = 1; i <= colCount; i++) {
					data.put(metaData.getColumnName(i), resultSet.getObject(i));
				}
				out.add(data);
			}
		} catch (SQLException e) {
			throw new GrainDbStatementException(e);
		}
		return out;
	}
}
