package com._7aske.grain.web.controller.parameter;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.lang.reflect.Parameter;

public interface ParameterConverter {
    boolean supports(Parameter parameter);

    default Object convert(Parameter parameter, HttpRequest request, HttpResponse response) {
        return convert(parameter, request, response, null);
    }

    Object convert(Parameter parameter, HttpRequest request, HttpResponse response, RequestHandler handler);
}
