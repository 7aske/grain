package com._7aske.grain;

import com._7aske.grain.config.Configuration;
import com._7aske.grain.config.ConfigurationBuilder;
import com._7aske.grain.config.GrainApplication;
import com._7aske.grain.context.ApplicationContext;
import com._7aske.grain.context.ApplicationContextImpl;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.requesthandler.RequestHandlerRunnable;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Application class responsible for setting up the application context
 * and providing configuration utilities for its derived classes. In user
 * defined application there should be a class that inherits this one and that gets
 * passed to GrainApplicationRunner
 */
@GrainApplication
public abstract class GrainApp {
	private Configuration configuration;
	private StaticLocationsRegistry staticLocationsRegistry;
	private boolean running = true;
	private ApplicationContext context;

	private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

	protected GrainApp() {
	}

	// Package-private method that should be called from GrainAppRunner class
	final void run() {
		doRun();
	}

	// Must be called in order to initialize application context with all
	// required parameters and allow proper injection of configuration object
	final void initialize(String basePackage) {
		this.doConfigure();
		// initialize/reload context
		this.context = new ApplicationContextImpl(basePackage, configuration, staticLocationsRegistry);
	}

	// Main run loop
	private void doRun() {
		// @Refactor replace with logger
		System.err.printf("Started Grain application on %s:%d%n", configuration.getHost(), configuration.getPort());

		ExecutorService executor = Executors.newFixedThreadPool(configuration.getThreads());

		try (ServerSocket serverSocket = new ServerSocket(configuration.getPort(), -1, InetAddress.getByName(configuration.getHost()))) {

			while (running) {
				Socket socket = serverSocket.accept();
				executor.execute(new RequestHandlerRunnable(context, socket));
			}

		} catch (UnknownHostException e) {
			throw new AppInitializationException("Unable to resolve host " + configuration.getHost(), e);
		} catch (IOException e) {
			throw new AppInitializationException("Unable to create server socket", e);
		}
	}

	// Calling of all configuration methods should happen here as this method is being called
	// before initializing application context
	private void doConfigure() {
		this.configure(configurationBuilder);
		this.configuration = configurationBuilder.build();
		this.staticLocationsRegistry = StaticLocationsRegistry.createDefault();
		this.staticLocationRegistry(staticLocationsRegistry);
	}

	// Method used to allow derived class to modify configuration object
	// before it gets passed to application context
	protected void configure(ConfigurationBuilder builder) {
	}

	// Method used to allow derived class to modify static locations
	// before they get passed to application context
	protected void staticLocationRegistry(StaticLocationsRegistry registry) {
	}
}
