package com._7aske.grain.web.controller.response;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;

public abstract class AbstractResponseWriterSupport<T> implements ResponseWriter<T>    {
    protected final Class<T> type;

    protected AbstractResponseWriterSupport(Class<T> type) {
        this.type = type;
    }

    @Override
    public boolean supports(Object returnValue) {
        return type.isInstance(returnValue);
    }

    protected abstract void writeInternal(T returnValue, HttpResponse response, HttpRequest request, RequestHandler handler) throws IOException;

    public void write(Object returnValue, HttpResponse response, HttpRequest request, RequestHandler handler) throws IOException {
        writeInternal(type.cast(returnValue), response, request, handler);
    }
}
