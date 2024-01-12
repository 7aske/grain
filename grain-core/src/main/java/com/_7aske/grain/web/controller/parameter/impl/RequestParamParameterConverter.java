package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.constants.ValueConstants;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.reflect.ReflectionUtil;
import com._7aske.grain.web.http.RequestParams;
import com._7aske.grain.web.controller.annotation.RequestParam;
import com._7aske.grain.web.controller.converter.Converter;
import com._7aske.grain.web.controller.converter.ConverterRegistry;
import com._7aske.grain.web.controller.exception.RequestParameterRequiredException;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

        Class<?> type = parameter.getType();
        boolean isOptional = type.equals(Optional.class);
        if (isOptional) {
            type = ReflectionUtil.getGenericListTypeArgument(parameter);
        }

        String[] paramValues = requestParams.getArrayParameter(requestParam.value());
        if (paramValues.length == 0 && requestParam.required() && requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE) && !isOptional) {
            throw new RequestParameterRequiredException(requestParam.value());
        } else if ((paramValues.length == 0 || paramValues[0].isBlank())
                   && !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
            if (isOptional) {
                return Optional.empty();
            }
            paramValues = new String[]{requestParam.defaultValue()};
        }

        Object value = null;

        if (type.equals(String.class)) {
            value = String.join(",", paramValues);
        // @Todo create a converter for map and params type
//        } else if (type.equals(RequestParams.class)) {
//            return requestParams;
        } else if (List.class.isAssignableFrom(type)) {
            Class<?> genericType = ReflectionUtil.getGenericListTypeArgument(parameter, 0);
            value = Stream.of(paramValues)
                    .map(converterRegistry.getConverter(genericType)::convert)
                    .toList();
//        } else if (Map.class.isAssignableFrom(type)) {
//            if (ReflectionUtil.getGenericListTypeArgument(parameter, 1).isArray()) {
//                return requestParams.getParameters();
//            } else {
//                return requestParams.getParameters().entrySet().stream()
//                    .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        e -> String.join(",", e.getValue())
//                    ));
//            }
        } else if (type.isArray()) {
            Class<?> genericType = type.getComponentType();
            Object array = Array.newInstance(genericType, paramValues.length);
            for (int i = 0; i < paramValues.length; i++) {
                Array.set(array, i, converterRegistry.getConverter(genericType).convert(paramValues[i]));
            }
            value = array;

        } else if (converterRegistry.hasConverter(type)) {
            // RequestParams stores values as an array and returns only the
            // first element when getStringParameter is called, so we need to
            // join them back to a string in order to properly pass it to
            // converter for conversion.
            Converter<?> converter = converterRegistry.getConverter(type);
            String stringParam = String.join(",", paramValues);
            // If no value was submitted we cannot allow converter to
            // throw an exception. Converters expect valid values.
            if (!stringParam.isBlank()) {
                value = converter.convert(stringParam);
            }
        } else {
            value = paramValues[0];
        }

        if (isOptional) {
            return Optional.ofNullable(value);
        } else {
            return value;
        }

//        throw new GrainRuntimeException("Cannot convert parameter " + parameter.getName() + " of type " + type.getName());
    }
}
