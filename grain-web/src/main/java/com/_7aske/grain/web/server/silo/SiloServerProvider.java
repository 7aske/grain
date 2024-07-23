package com._7aske.grain.web.server.silo;

import com._7aske.grain.core.component.ConditionalOnMissingGrain;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.web.server.Server;

@Grain
@ConditionalOnMissingGrain(Server.class)
public class SiloServerProvider {

    @Grain
    @Order(Order.LOWEST_PRECEDENCE)
    public Server server(Configuration configuration, ApplicationContext context) {
        return new Silo(configuration, context);
    }

}
