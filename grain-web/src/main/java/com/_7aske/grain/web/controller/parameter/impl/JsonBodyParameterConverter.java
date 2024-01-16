package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.http.codec.json.annotation.JsonBody;

import java.io.IOException;
import java.lang.reflect.Parameter;

@Grain
public class JsonBodyParameterConverter implements ParameterConverter {
    private final JsonMapper jsonMapper;

    public JsonBodyParameterConverter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(JsonBody.class);
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
