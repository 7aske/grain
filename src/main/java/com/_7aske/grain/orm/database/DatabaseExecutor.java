package com._7aske.grain.orm.database;

import com._7aske.grain.component.Condition;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.orm.connection.ConnectionPool;
import com._7aske.grain.orm.connection.ConnectionWrapper;
import com._7aske.grain.orm.exception.GrainDbStatementException;

import java.sql.*;
import java.util.*;

import static com._7aske.grain.config.Configuration.Key.DATABASE_EXECUTOR_PRINT_SQL;

@Grain
@Condition("grain.persistence.provider == 'native'")
public class DatabaseExecutor {
	@Inject
	protected ConnectionPool connectionPool;
	@Inject
	protected Configuration configuration;
	private final Logger logger = LoggerFactory.getLogger(DatabaseExecutor.class);

	// Used for update and insert operations
	public long executeUpdate(String query) {
		if (Objects.equals(configuration.getProperty(DATABASE_EXECUTOR_PRINT_SQL), true)) {
			logger.trace(query);
		}
		try (ConnectionWrapper connection = connectionPool.getConnection(); Statement statement = connection.get().createStatement()) {
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
			logger.trace(query);
		}
		List<Map<String, String>> out = new ArrayList<>();
		try (ConnectionWrapper connection = connectionPool.getConnection(); Statement statement = connection.get().createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			ResultSetMetaData metaData = resultSet.getMetaData();
			while (resultSet.next()) {
				Map<String, String> data = new HashMap<>();
				int colCount = metaData.getColumnCount();
				for (int i = 1; i <= colCount; i++) {
					// @Note use getColumnLabel instead of getColumnName
					// because we're using aliases for referencing joined
					// column names.
					data.put(getScopedColumnName(metaData, i), resultSet.getString(i));
				}
				out.add(data);
			}
		} catch (SQLException e) {
			throw new GrainDbStatementException(e);
		}
		return out;
	}

	private String getScopedColumnName(ResultSetMetaData metaData, int i) throws SQLException {
		return String.format("%s.%s", metaData.getTableName(i), metaData.getColumnName(i));
	}
}
