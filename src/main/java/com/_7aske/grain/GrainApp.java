package com._7aske.grain;

import com._7aske.grain.config.Configuration;
import com._7aske.grain.config.ConfigurationKey;
import com._7aske.grain.config.GrainApplication;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
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
public class GrainApp {
	private Configuration configuration;
	private StaticLocationsRegistry staticLocationsRegistry;
	private boolean running = true;
	private ApplicationContext context;
	private final Logger logger = LoggerFactory.getLogger(GrainApp.class);

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
		// After initializing the application context we set it to the holder to make it
		// available for use in other classes that are not available for dependency injection.
		ApplicationContextHolder.setContext(this.context);
		logger.info("Initialized application context");
	}

	// Main run loop
	private void doRun() {
		logger.info("Started Grain application on {}:{}", configuration.get(ConfigurationKey.SERVER_HOST), configuration.get(ConfigurationKey.SERVER_PORT));

		ExecutorService executor = Executors.newFixedThreadPool(configuration.getInt(ConfigurationKey.SERVER_THREADS));

		try (ServerSocket serverSocket = new ServerSocket(configuration.getInt(ConfigurationKey.SERVER_PORT), -1, InetAddress.getByName(configuration.get(ConfigurationKey.SERVER_HOST)))) {

			while (running) {
				Socket socket = serverSocket.accept();
				executor.execute(new RequestHandlerRunnable(context, socket));
			}

		} catch (UnknownHostException e) {
			throw new AppInitializationException("Unable to resolve host " + configuration.get(ConfigurationKey.SERVER_HOST), e);
		} catch (IOException e) {
			throw new AppInitializationException("Unable to create server socket", e);
		}
	}

	// Calling of all configuration methods should happen here as this method is being called
	// before initializing application context
	private void doConfigure() {
		configuration = Configuration.createDefault();
		this.configure(configuration);
		this.staticLocationsRegistry = StaticLocationsRegistry.createDefault();
		this.staticLocationRegistry(staticLocationsRegistry);
	}

	// Method used to allow derived class to modify configuration object
	// before it gets passed to application context
	protected void configure(Configuration configuration) {
	}

	// Method used to allow derived class to modify static locations
	// before they get passed to application context
	protected void staticLocationRegistry(StaticLocationsRegistry registry) {
	}
}
