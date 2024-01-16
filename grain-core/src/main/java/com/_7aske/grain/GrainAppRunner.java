package com._7aske.grain;

import com._7aske.grain.core.ApplicationEntryPoint;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.core.reflect.ReflectionUtil;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.logging.LoggingConfigurer;

/**
 * Grain application runner responsible for handling initialization of the
 * application itself by passing the required package argument.
 */
public final class GrainAppRunner {
    private static final Logger logger = LoggerFactory.getLogger(GrainAppRunner.class);
    private static final Configuration configuration = Configuration.createDefault();
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
        ApplicationEntryPoint entryPoint = context.getGrain(ApplicationEntryPoint.class);
        entryPoint.run();
    }

    // Must be called in order to initialize application context with all
    // required parameters and allow proper injection of configuration object
    static void initialize(Class<?> clazz) {
        LoggingConfigurer.configure();

        if (GrainApp.class.isAssignableFrom(clazz)) {
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
}