package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.util.RequestParams;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.form.FormBody;
import com._7aske.grain.web.http.codec.form.FormDataMapper;

import java.lang.reflect.Parameter;
import java.util.Map;

@Grain
public class FormBodyParameterConverter implements ParameterConverter {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(FormBody.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, com._7aske.grain.web.requesthandler.handler.RequestHandler handler) {
        // Mapping request params from HttpRequest.parameters to either
        // a Map<String, String> or a class specified by the method parameter.
        // This case is different from other because we have to extract
        // the data using request.getParameters instead of request.getBody.
        if (Map.class.isAssignableFrom(parameter.getType())) {
            return request.getParameterMap();
        }

        if (RequestParams.class.isAssignableFrom(parameter.getType())) {
            return new RequestParams(request.getParameterMap());
        }

        return new FormDataMapper<>(parameter.getType()).parse(request.getParameterMap());
    }
}
