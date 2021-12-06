package com._7aske.grain.orm.connection;

import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com._7aske.grain.config.Configuration.Key;

@Grain
public class ConnectionPool {
	final List<ConnectionWrapper> connections;
	@Inject
	private ConnectionManager connectionManager;
	private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

	public ConnectionPool(Configuration configuration) {
		Integer numCon = configuration.getProperty(Key.DATABASE_POOL_SIZE, 10);
		connections = new ArrayList<>(numCon);
		for (int i = 0; i < numCon; i++) {
			connections.add(new ConnectionWrapper());
		}
		logger.info("Initialized {} pool connections", numCon);
	}

	public void restartConnections() {
		for (ConnectionWrapper connection : connections) {
			if (!connection.isActive() && !connection.isWorking())
				connection.restart(connectionManager);
		}
	}

	// @Incomplete @Bug Does return null instances with null Connections
	// in cases where tons of requests are sent.
	public synchronized ConnectionWrapper getConnection() {
		synchronized (connections) {
			restartConnections();
			Optional<ConnectionWrapper> connWrap = connections.stream()
					.filter(c -> c.isActive() && !c.isWorking())
					.findFirst();
			if (connWrap.isEmpty()) {
				ConnectionWrapper newConn = new ConnectionWrapper();
				connections.add(newConn);
				newConn.setWorking(true);
				logger.warn("Adding a new connection");
				return newConn;
			}
			ConnectionWrapper connectionWrapper = connWrap.get();
			connectionWrapper.setWorking(true);
			return connectionWrapper;
		}
	}
}
