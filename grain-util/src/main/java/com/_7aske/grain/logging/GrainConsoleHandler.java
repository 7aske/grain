package com._7aske.grain.logging;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * Grain specific console handler that redirects all output to STDOUT.
 */
public class GrainConsoleHandler extends StreamHandler {
	public GrainConsoleHandler() {
		super(System.out, new SimpleFormatter());
		// This seems to be unconfigurable by LogManager, so we have to set a high
		// value and let individual loggers handle their own level. Bottom line:
		// Regardless of what level we use for the logger since logger sends their
		// LogRecords to the handler handles is the one that ultimately rejects them
		// because it's default level is Level.INFO.
		setLevel(java.util.logging.Level.ALL);
	}

	@Override
	public synchronized void publish(LogRecord logRecord) {
		super.publish(logRecord);
		flush();
	}

	@Override
	public synchronized void close() {
		flush();
	}
}
