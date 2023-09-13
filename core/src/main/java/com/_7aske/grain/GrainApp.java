package com._7aske.grain;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

/**
 * Optional base class used to overrider and provide programmatic access to
 * application configuration.
 */
public class GrainApp {
	private final Logger logger = LoggerFactory.getLogger(GrainApp.class);

	protected GrainApp() {
	}

	// Method used to allow derived class to modify configuration object
	// before it gets passed to application context
	protected void configure(Configuration configuration) {
		logger.debug("Default application configuration called");
	}
}
