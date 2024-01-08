package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.HttpStatus;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;
import java.util.Objects;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
public class NullResponseWriter extends AbstractResponseWriterSupport<Void> {
    protected NullResponseWriter() {
        super(Void.class);
    }

    @Override
    public boolean supports(Object returnValue) {
        return Objects.isNull(returnValue);
    }

    @Override
    public void writeInternal(Void returnValue, HttpResponse response, HttpRequest request, RequestHandler handler) throws IOException {
        String requestContentType = request.getHeader(CONTENT_TYPE);
        response.setHeader(CONTENT_TYPE, requestContentType == null ? ContentType.TEXT_PLAIN : requestContentType);
        response.setStatus(HttpStatus.NO_CONTENT);
    }
}
