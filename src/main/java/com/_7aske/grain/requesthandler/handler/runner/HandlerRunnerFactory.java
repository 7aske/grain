package com._7aske.grain.requesthandler.handler.runner;


import com._7aske.grain.requesthandler.handler.proxy.factory.HandlerProxyFactory;

public class HandlerRunnerFactory {
	private HandlerRunnerFactory(){}

	public static HandlerRunner getRunner(HandlerProxyFactory factory) {
		return new HandlerRunner(factory);
	}
}
