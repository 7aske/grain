package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.util.HttpPathUtil;
import com._7aske.grain.web.controller.annotation.PathVariable;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.lang.reflect.Parameter;

@Grain
public class PathVariableParameterConverter implements ParameterConverter {
    private final ConverterRegistry converterRegistry;

    public PathVariableParameterConverter(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, RequestHandler handler) {
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        String value = HttpPathUtil.resolvePathVariableValue(request.getPath(), handler.getPath(), pathVariable);
        if (value == null) {
            return null;
        }

        if (converterRegistry.hasConverter(parameter.getType())) {
            return converterRegistry.getConverter(parameter.getType()).convert(value);
        }

       return value;
    }
}
