package com._7aske.grain.web.controller.exceptionhandler;


import com._7aske.grain.core.component.AnnotatedBy;
import com._7aske.grain.core.component.ExceptionController;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.util.Pair;
import com._7aske.grain.web.controller.ResponseStatusResolver;
import com._7aske.grain.web.controller.annotation.ResponseStatus;
import com._7aske.grain.web.controller.response.ResponseWriter;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.HttpStatus;
import com._7aske.grain.web.requesthandler.controller.wrapper.ExceptionControllerMethodWrapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Grain
public final class ExceptionControllerHandler {
    private final List<ExceptionControllerMethodWrapper> exceptionHandlers;
    private final ResponseWriterRegistry responseWriterRegistry;

    public ExceptionControllerHandler(@AnnotatedBy(ExceptionController.class) List<Object> exceptionHandlers,
                                      ResponseWriterRegistry responseWriterRegistry) {
        this.exceptionHandlers = exceptionHandlers.stream()
                .map(controller -> Pair.of(
                        controller,
                        Arrays.stream(controller.getClass().getDeclaredMethods())
                                .filter(method -> method.isAnnotationPresent(ExceptionHandler.class)))
                )
                .flatMap(controllerAndMethods -> controllerAndMethods.getSecond()
                        .map(method -> new ExceptionControllerMethodWrapper(method, controllerAndMethods.getFirst())))
                .sorted(Comparator.comparingInt(ExceptionControllerMethodWrapper::getOrder))
                .toList();
        this.responseWriterRegistry = responseWriterRegistry;
    }

    public void handle(Throwable ex, HttpRequest request, HttpResponse response) {
        ex.printStackTrace();

        // Default status for exceptions
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        // Lowest priority - check if exception is annotated with ResponseStatus
        setResponseStatus(ex.getClass().getAnnotation(ResponseStatus.class), response);

        exceptionHandlers.stream()
                .filter(handler -> handler.canHandle(ex))
                .findFirst()
                .ifPresent(handler -> {

                    // Higher priority - check if handler is annotated with ResponseStatus
                    setResponseStatus(handler.getResponseStatus(), response);

                    try {
                        // Highest priority - can be set within the handler itself
                        Object result = handler.invoke(ex, request, response);
                        if (handler.isVoidReturnType()) {
                            return;
                        }
                        Optional<ResponseWriter<?>> optionalWriter = responseWriterRegistry.getWriter(result);
                        if (optionalWriter.isEmpty()){
                            return;
                        }
                        optionalWriter.get().write(result, request, response, null);
                    } catch (Exception e) {
                        throw new GrainRuntimeException(e);
                    }
                });
    }

    private static void setResponseStatus(ResponseStatus responseStatus, HttpResponse response) {
        if (responseStatus != null) {
            response.setStatus(ResponseStatusResolver.resolveStatus(responseStatus));
        }
    }
}
