package com._7aske.grain;

import com._7aske.grain.component.Controller;
import com._7aske.grain.config.ConfigurationBuilder;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class GrainAppTest {
	ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Controller
	static class TestController {
		public String get(){
			return "Test";
		}
	}

	static class TestApp extends GrainApp {
		@Override
		protected void configure(ConfigurationBuilder builder) {
			builder.port(7070);
		}
	}

	@Test
	void test_run() {
		boolean passed = false;
		RunnableFuture<Void> runnableFuture = new FutureTask<>(() -> {
			GrainAppRunner.run(TestApp.class);
			return null;
		});

		executorService.execute(runnableFuture);

		try {
			runnableFuture.get(2, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			runnableFuture.cancel(true);
			passed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		executorService.shutdown();
		assertTrue(passed);
	}

	static final int testPort = 36265;

	static class TestConfigurationApp extends GrainApp {
		@Override
		protected void configure(ConfigurationBuilder builder) {
			builder
					.port(testPort)
					.host("127.0.0.1");
		}
	}

	@Test
	void test_configuration() throws InterruptedException {
		executorService.execute(() -> GrainAppRunner.run(TestConfigurationApp.class));
		try {
			Thread.sleep(1000);
			new ServerSocket(testPort);
			fail();
		} catch (IOException e) {
			assertTrue(true);
		}
	}

	static class DebugApp extends GrainApp {
		@Override
		protected void staticLocationRegistry(StaticLocationsRegistry registry) {
			registry.addStaticLocation("/home/nik/.local/src/js/website/build");
		}

		@Override
		protected void configure(ConfigurationBuilder builder) {
			builder.port(33631);
		}

	}


	@Test
	@Disabled("Used for debugging purposes")
	void test_debug() {
		GrainAppRunner.run(DebugApp.class);
		assertTrue(true);
	}
}