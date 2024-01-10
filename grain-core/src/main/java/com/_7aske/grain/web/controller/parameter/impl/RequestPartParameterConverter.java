package com._7aske.grain.web.controller.parameter.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.annotation.RequestPart;
import com._7aske.grain.web.controller.parameter.ParameterConverter;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.multipart.Part;
import com._7aske.grain.web.http.multipart.exception.MultipartRequiredException;

import java.lang.reflect.Parameter;

@Grain
public class RequestPartParameterConverter implements ParameterConverter {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestPart.class);
    }

    @Override
    public Object convert(Parameter parameter, HttpRequest request, HttpResponse response, com._7aske.grain.web.requesthandler.handler.RequestHandler handler) {
        RequestPart requestPart = parameter.getAnnotation(RequestPart.class);
        Part part = request.getPart(requestPart.value());
        if (requestPart.required() && part == null) {
            throw new MultipartRequiredException(requestPart);
        }

        return part;
    }
}
