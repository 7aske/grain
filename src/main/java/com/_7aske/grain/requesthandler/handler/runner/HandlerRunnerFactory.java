package com._7aske.grain.requesthandler.handler.runner;


import com._7aske.grain.requesthandler.handler.HandlerRegistry;

import java.util.ArrayList;

public class HandlerRunnerFactory {
	private HandlerRunnerFactory(){}

	public static <T extends HandlerRegistry> HandlerRunner<T> getRunner() {
		return new HandlerRunner<>(new ArrayList<>());
	}
}
