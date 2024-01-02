package com._7aske.grain.web.server;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Silo is an attempt at an application/web server for Grain. Handles only HTTP requests - barely.
 */
public class Silo {
    private final Logger logger = LoggerFactory.getLogger(Silo.class);
    private final Configuration configuration;
    private final ApplicationContext context;

    public Silo(Configuration configuration, ApplicationContext context) {
        this.configuration = configuration;
        this.context = context;
    }

    public void run() {
        logger.info("Started Grain application on {}:{}", configuration.get(ConfigurationKey.SERVER_HOST), configuration.get(ConfigurationKey.SERVER_PORT));

        ExecutorService executor = Executors.newFixedThreadPool(configuration.getInt(ConfigurationKey.SERVER_THREADS));

        try (ServerSocket serverSocket = new ServerSocket(configuration.getInt(ConfigurationKey.SERVER_PORT), -1, InetAddress.getByName(configuration.get(ConfigurationKey.SERVER_HOST)))) {

            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new RequestHandlerRunnable(context, socket));
            }

        } catch (UnknownHostException e) {
            throw new AppInitializationException("Unable to resolve host " + configuration.get(ConfigurationKey.SERVER_HOST), e);
        } catch (IOException e) {
            throw new AppInitializationException("Unable to create server socket", e);
        }

    }
}
