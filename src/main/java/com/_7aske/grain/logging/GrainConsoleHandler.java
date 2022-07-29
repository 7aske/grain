package com._7aske.grain.logging;

import java.util.logging.ConsoleHandler;

public class GrainConsoleHandler extends ConsoleHandler {
	public GrainConsoleHandler() {
		setOutputStream(System.out);
	}
}
