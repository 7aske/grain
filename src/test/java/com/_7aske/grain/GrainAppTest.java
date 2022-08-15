package com._7aske.grain;

import com._7aske.grain.core.component.Controller;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.web.controller.annotation.RequestMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
class GrainAppTest {
	ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Controller
	@RequestMapping
	static class TestController {
		public String get(){
			return "Test";
		}
	}

	static class TestApp extends GrainApp {
		@Override
		protected void configure(Configuration builder) {
			builder.set("server.port", 7070);
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
		protected void configure(Configuration builder) {
			builder
			.set("server.port", 7070)
			.set("server.host", "127.0.0.1");
		}
	}

	@BeforeEach
	void setup() {
		ApplicationContextHolder.setContext(null);
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
		protected void configure(Configuration builder) {
			builder.set("server.port", 33631);
		}

	}


	@Test
	@Disabled("Used for debugging purposes")
	void test_debug() {
		GrainAppRunner.run(DebugApp.class);
		assertTrue(true);
	}
}