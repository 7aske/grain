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

@GrainApplication
public abstract class GrainApp {
	private Configuration configuration;
	private String basePackage;
	private boolean running = true;
	private ApplicationContext context;

	private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

	protected GrainApp() {
	}

	final void run() {
		doConfigure();
		doRun();
	}

	private void doRun() {
		// TODO: replace with logger
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

	private void doConfigure() {
		this.configure(configurationBuilder);
		this.configuration = configurationBuilder.build();
		this.staticLocationRegistry(context.getStaticLocationsRegistry());
	}

	protected void configure(ConfigurationBuilder builder) {
	}

	protected void staticLocationRegistry(StaticLocationsRegistry registry) {
	}

	final void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
		// reload context
		this.context = new ApplicationContextImpl(basePackage);
	}
}
