package com._7aske.grain.security.handler.proxy.factory;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityHandlerProxyFactoryTest {
	ApplicationContext applicationContext;
	static class TestClass extends GrainApp {

	}

	@BeforeEach
	void setUp() {
		Configuration configuration = Configuration.createDefault();
		configuration.set("security.enabled", true);
		applicationContext = new ApplicationContextImpl(TestClass.class.getPackageName(), configuration);
		ApplicationContextHolder.setContext(applicationContext);
	}

	@AfterEach
	void tearDown() {
		ApplicationContextHolder.setContext(null);
	}

	@Test
	void testCreateProxy() {
		HandlerProxyFactory factory = applicationContext.getGrain(HandlerProxyFactory.class);

		assertNotNull(factory);
		assertTrue(factory instanceof SecurityHandlerProxyFactory);
	}
}