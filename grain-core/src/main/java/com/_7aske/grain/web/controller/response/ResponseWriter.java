package com._7aske.grain.web.controller.response;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;

public interface ResponseWriter<T> {
    // @Todo add support for accept header or produces annotation value
    boolean supports(Object returnValue);

    void write(Object returnValue, HttpResponse response, HttpRequest request, RequestHandler handler) throws IOException;
}
