package com._7aske.grain.orm.connection;

import com._7aske.grain.component.Condition;
import com._7aske.grain.component.Grain;
import com._7aske.grain.component.Inject;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.formatter.StringFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com._7aske.grain.config.Configuration.Key;

@Grain
@Condition("grain.persistence.provider == 'native'")
public class ConnectionPool {
	final List<ConnectionWrapper> connections;
	@Inject
	private ConnectionManager connectionManager;
	private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
	private final Lock lock = new ReentrantLock();
	private Integer poolConnectionWait;

	public ConnectionPool(Configuration configuration) {
		Integer numConnections = configuration.getProperty(Key.DATABASE_POOL_SIZE, 10);
		poolConnectionWait = configuration.getProperty(Key.DATABASE_POOL_CONNECTION_WAIT, 10);
		connections = new ArrayList<>(numConnections);
		for (int i = 0; i < numConnections; i++) {
			connections.add(new ConnectionWrapper());
		}
		logger.info("Initialized {} pool connections", numConnections);
	}

	public void restartConnections() {
		for (ConnectionWrapper connection : connections) {
			if (!connection.isActive() && !connection.isWorking())
				connection.restart(connectionManager);
		}
	}

	public ConnectionWrapper getConnection() {
		lock.lock();
		try {

			restartConnections();
			ConnectionWrapper connWrap = null;
			while (connWrap == null) {
				connWrap = connections.stream()
						.filter(c -> c.isActive() && !c.isWorking())
						.findFirst()
						.orElse(null);
				if (connWrap == null){
					logger.warn(StringFormat.format("No free connection. Waiting {}ms...", poolConnectionWait));
					try {
						// @Temporary We sleep the main thread here :(
						Thread.sleep(poolConnectionWait);
					} catch (InterruptedException ignored) {
						// ignored
					}
				}
			}
			connWrap.setWorking(true);
			return connWrap;
		} finally {
			lock.unlock();
		}
	}
}
