package com._7aske.grain.web.server;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

public abstract class Server {
    protected final Logger logger = LoggerFactory.getLogger(Server.class);
    protected final Configuration configuration;
    protected final ApplicationContext context;

    protected Server(Configuration configuration, ApplicationContext context) {
        this.configuration = configuration;
        this.context = context;
    }

    public abstract void run() throws AppInitializationException;
}
