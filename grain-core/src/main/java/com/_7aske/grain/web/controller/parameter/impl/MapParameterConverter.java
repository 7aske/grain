package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Map;

@Grain
public class MapParameterConverter implements ParameterConverter {
    private final JsonMapper jsonMapper;

    public MapParameterConverter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return Map.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, com._7aske.grain.web.requesthandler.handler.RequestHandler handler) {
        String body = null;
        try {
            body = new String(request.getInputStream().readAllBytes(), request.getCharacterEncoding());
        } catch (IOException e) {
            // @Todo throw custom exception
            throw new GrainRuntimeException(e);
        }

        return jsonMapper.mapValue(body, parameter);
    }
}
