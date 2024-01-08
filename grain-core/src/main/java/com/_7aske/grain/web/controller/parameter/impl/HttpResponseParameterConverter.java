package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;

import java.lang.reflect.Parameter;

@Grain
public class HttpResponseParameterConverter implements ParameterConverter {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.getType().equals(HttpResponse.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, com._7aske.grain.web.requesthandler.handler.RequestHandler handler) {
        return response;
    }
}
