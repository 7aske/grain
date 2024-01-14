package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.GrainHttpRequest;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.RequestParams;
import com._7aske.grain.web.http.codec.form.FormBody;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.server.HttpRequestReader;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Grain
public class MapParameterConverter implements ParameterConverter {
    @Override
    public boolean supports(Parameter parameter) {
        return Map.class.isAssignableFrom(parameter.getType()) && !parameter.isAnnotationPresent(FormBody.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, RequestHandler handler) {
        return request.getParameterMap();
    }
}
