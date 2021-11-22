package com._7aske.grain.orm.connection;

import com._7aske.grain.GrainApp;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.config.ConfigurationKey;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.context.ApplicationContextImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {
	public static final class TestApp extends GrainApp {

	}


	@Test
	void testGetConnectionUrl() {
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApp.class.getPackageName());
		Configuration configuration = applicationContext.getGrainRegistry().getGrain(Configuration.class);
		configuration.getProperties().put(ConfigurationKey.DATABASE_HOST, "127.0.0.1");
		configuration.getProperties().put(ConfigurationKey.DATABASE_PORT, 3306);
		configuration.getProperties().put(ConfigurationKey.DATABASE_NAME, "test");
		ConnectionManager connectionManager = applicationContext.getGrainRegistry().getGrain(ConnectionManager.class);
		Assertions.assertEquals("mysql://127.0.0.1:3306/test", connectionManager.getConnectionUrl());
	}
}