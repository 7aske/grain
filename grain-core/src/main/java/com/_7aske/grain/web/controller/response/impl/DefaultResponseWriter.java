package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;

import static com._7aske.grain.web.http.HttpHeaders.ACCEPT;
import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultResponseWriter extends AbstractResponseWriterSupport<Object> {
    private final JsonMapper jsonMapper;

    protected DefaultResponseWriter(JsonMapper jsonMapper) {
        super(Object.class);
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(Object returnValue) {
        return true;
    }

    @Override
    public void writeInternal(Object returnValue, HttpRequest request, HttpResponse response, RequestHandler handler) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            if (request.getHeader(ACCEPT).equals(ContentType.APPLICATION_JSON)) {
                response.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON);
                jsonMapper.writeValue(jsonMapper.mapValue(returnValue), outputStream);
            } else {
                outputStream.write(returnValue.toString().getBytes(request.getCharacterEncoding()));
            }

        }
    }
}
