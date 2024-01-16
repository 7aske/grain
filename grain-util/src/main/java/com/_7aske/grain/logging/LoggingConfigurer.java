package com._7aske.grain.logging;

import com._7aske.grain.properties.PropertiesResolver;

import java.util.List;

import static java.util.logging.LogManager.getLogManager;

public class LoggingConfigurer {
    private LoggingConfigurer() {}
    private static final String[] FILE_PATHS = {"META-INF/logging", "logging"};

    public static void configure() {
        // Profile-less configuration of LogManager.
        PropertiesResolver propertiesResolver = new PropertiesResolver(List.of());
        propertiesResolver.resolve(FILE_PATHS, is -> {
            // We create a copy of read bytes since we need to use the input stream
            // twice. Once for properties  and once for the LogManager itself.
            // InputStream returned from PropertiesResolver#resolve() cannot be
            // reset to be read from again.
            LoggerFactory.getProperties().load(is);
            is.reset();
            getLogManager().readConfiguration(is);
            is.close();
        });
    }
}
