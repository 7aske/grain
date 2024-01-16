package com._7aske.grain.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

	@Test
	void testLogger() {
		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		logger.error("{0}", "hello");
	}
}