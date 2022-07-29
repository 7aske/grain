package com._7aske.grain.orm.connection;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com._7aske.grain.config.ConfigurationKey.*;

class ConnectionManagerTest {
	public static final class TestApp extends GrainApp {

	}


	@Test
	void testGetConnectionUrl() {
		ApplicationContextHolder.setContext(null);
		Configuration configuration = Configuration.createDefault();
		configuration.setProperty(DATABASE_HOST, "127.0.0.1");
		configuration.setProperty(DATABASE_PORT, 3306);
		configuration.setProperty(DATABASE_NAME, "test");
		configuration.setPropertyUnsafe("grain.persistence.provider", "native");
		ApplicationContext applicationContext = new ApplicationContextImpl(TestApp.class.getPackageName(),
				configuration,
				StaticLocationsRegistry.createDefault());
		ConnectionManager connectionManager = applicationContext.getGrainRegistry().getGrain(ConnectionManager.class);
		Assertions.assertEquals("jdbc:mysql://127.0.0.1:3306/test", connectionManager.getConnectionUrl());
	}
}