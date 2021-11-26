package com._7aske.grain.orm.connection;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.context.ApplicationContextImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com._7aske.grain.config.Configuration.Key.*;

class ConnectionManagerTest {
	public static final class TestApp extends GrainApp {

	}


	@Test
	void testGetConnectionUrl() {
		ApplicationContextHolder.setContext(null);
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApp.class.getPackageName());
		Configuration configuration = applicationContext.getGrainRegistry().getGrain(Configuration.class);
		configuration.setProperty(DATABASE_HOST, "127.0.0.1");
		configuration.setProperty(DATABASE_PORT, 3306);
		configuration.setProperty(DATABASE_NAME, "test");
		ConnectionManager connectionManager = applicationContext.getGrainRegistry().getGrain(ConnectionManager.class);
		Assertions.assertEquals("jdbc:mysql://127.0.0.1:3306/test", connectionManager.getConnectionUrl());
	}
}