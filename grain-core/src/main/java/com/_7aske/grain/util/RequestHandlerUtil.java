package com._7aske.grain.util;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.util.Collection;
import java.util.Optional;

public class RequestHandlerUtil {
    private RequestHandlerUtil() {
    }

    public static <T extends RequestHandler> Optional<T> getBestHandler(HttpRequest request, Collection<T> handlers) {
        return handlers.stream()
                .filter(methodHandler -> methodHandler.canHandle(request))
                .max(By.pathLength());
    }
}
