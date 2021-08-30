package com._7aske.grain;

import com._7aske.grain.component.GrainRegistry;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.config.ConfigurationBuilder;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.handler.RequestHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GrainApp {
	private Configuration configuration;
	private String basePackage;
	private boolean running = true;

	private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
	private GrainRegistry grainRegistry;

	protected GrainApp() {
	}

	void run() {
		doConfigure();
		doRegisterGrains();
		doRun();
	}

	private void doRun() {
		// TODO: replace with logger
		System.err.printf("Started Grain application on %s:%d%n", configuration.getHost(), configuration.getPort());

		ExecutorService executor = Executors.newFixedThreadPool(configuration.getThreads());

		try (ServerSocket serverSocket = new ServerSocket(configuration.getPort(), -1, InetAddress.getByName(configuration.getHost()))) {

			while (running) {
				Socket socket = serverSocket.accept();
				executor.execute(RequestHandler.handle(grainRegistry, socket));
			}

		} catch (UnknownHostException e) {
			throw new AppInitializationException("Unable to resolve host " + configuration.getHost(), e);
		} catch (IOException e) {
			throw new AppInitializationException("Unable to create server socket", e);
		}
	}

	private void doRegisterGrains() {
		grainRegistry = new GrainRegistry(basePackage);
	}

	private void doConfigure() {
		this.configure(configurationBuilder);
		this.configuration = configurationBuilder.build();
	}

	protected void configure(ConfigurationBuilder builder) {
	}

	public final String getBasePackage() {
		return basePackage;
	}

	final void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}
