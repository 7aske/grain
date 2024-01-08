package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.constants.ValueConstants;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.util.RequestParams;
import com._7aske.grain.web.controller.annotation.RequestParam;
import com._7aske.grain.web.controller.converter.Converter;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;

import java.lang.reflect.Parameter;

@Grain
public class RequestParamParameterConverter implements ParameterConverter {
    private final ConverterRegistry converterRegistry;

    public RequestParamParameterConverter(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, com._7aske.grain.web.requesthandler.handler.RequestHandler handler) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        RequestParams requestParams = new RequestParams(request.getParameterMap());

        String[] paramValues = requestParams.getArrayParameter(requestParam.value());
        if ((paramValues.length == 0 || paramValues[0].isBlank())
            && !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
            paramValues = new String[]{requestParam.defaultValue()};
        }

        if (parameter.getType().equals(String.class)) {
            return String.join(",", paramValues);
        } else if (parameter.getType().isArray()) {
            return paramValues;
        } else if (converterRegistry.hasConverter(parameter.getType())) {
            // RequestParams stores values as an array and returns only the
            // first element when getStringParameter is called, so we need to
            // join them back to a string in order to properly pass it to
            // converter for conversion.
            Converter<?> converter = converterRegistry.getConverter(parameter.getType());
            String stringParam = String.join(",", paramValues);
            // If no value was submitted we cannot allow converter to
            // throw an exception. Converters expect valid values.
            if (stringParam.isBlank()) {
                // If the value is primitive we don't do anything. You
                // shouldn't define values as primitive anyway.
                if (!parameter.getClass().isPrimitive()) {
                    return null;
                }
            } else {
                return converter.convert(stringParam);
            }
        } else {
            return paramValues[0];
        }

        throw new GrainRuntimeException("Cannot convert parameter " + parameter.getName() + " of type " + parameter.getType().getName());
    }
}
