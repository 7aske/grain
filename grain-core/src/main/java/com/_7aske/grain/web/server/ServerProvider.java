package com._7aske.grain.web.server;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.web.server.silo.Silo;

@Grain
public class ServerProvider {

    public Server server(Configuration configuration, ApplicationContext context) {
        return new Silo(configuration, context);
    }

}
