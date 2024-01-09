package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultResponseWriter extends AbstractResponseWriterSupport<Object> {

    protected DefaultResponseWriter() {
        super(Object.class);
    }

    @Override
    public boolean supports(Object returnValue) {
        return true;
    }

    @Override
    public void writeInternal(Object returnValue, HttpRequest request, HttpResponse response, RequestHandler handler) throws IOException {
        response.getOutputStream().write(returnValue.toString().getBytes());
        if (response.getHeader(CONTENT_TYPE) == null) {
            response.setHeader(CONTENT_TYPE, ContentType.TEXT_PLAIN);
        }
    }
}
