package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.session.Session;

import java.lang.reflect.Parameter;

@Grain
public class HttpSessionParameterConverter implements ParameterConverter {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.getType().equals(Session.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, com._7aske.grain.web.requesthandler.handler.RequestHandler handler) {
        return request.getSession();
    }
}
