package com._7aske.grain;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.requesthandler.RequestHandlerRunnable;
import com._7aske.grain.util.ReflectionUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Grain application runner responsible for handling initialization of the
 * application itself by passing the required package argument.
 */
public final class GrainAppRunner {
    private static final Logger logger = LoggerFactory.getLogger(GrainAppRunner.class);
    private static final Configuration configuration = Configuration.createDefault();
    private static boolean running = true;
    private static ApplicationContext context;

    private GrainAppRunner() {
    }

    public static void run(Class<?> clazz) {
        final long startTime = System.currentTimeMillis();
        try {
            initialize(clazz);
            logger.debug("Startup took {}ms", System.currentTimeMillis() - startTime);
            run();
        } catch (Exception e) {
            throw new AppInitializationException("Failed to initialize Grain App", e);
        }
    }

    // Package-private method that should be called from GrainAppRunner class
    static void run() {
        doRun();
    }

    // Must be called in order to initialize application context with all
    // required parameters and allow proper injection of configuration object
    static void initialize(Class<?> clazz) {
        if (GrainApp.class.isAssignableFrom(clazz)) {
            // @Temporary until I figure out have to implement @Grain methods
            //   that can be used to re-configure grains.
            GrainApp instance = (GrainApp) ReflectionUtil.newInstance(clazz);
            instance.configure(configuration);
        }

        // initialize/reload context
        context = new ApplicationContextImpl(clazz.getPackageName(), configuration);
        // After initializing the application context we set it to the holder to make it
        // available for use in other classes that are not available for dependency injection.
        ApplicationContextHolder.setContext(context);
        logger.info("Initialized application context");
    }

    // Main run loop
    static void doRun() {
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

}
