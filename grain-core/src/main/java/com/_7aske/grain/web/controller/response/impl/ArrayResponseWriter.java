package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
public class ArrayResponseWriter extends AbstractResponseWriterSupport<Object[]> {
    private final JsonMapper jsonMapper;

    public ArrayResponseWriter(JsonMapper jsonMapper) {
        super(Object[].class);
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void writeInternal(Object[] returnValue, HttpResponse response, HttpRequest request, RequestHandler handler) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            jsonMapper.writeValue(jsonMapper.mapValue(returnValue), outputStream);
        }
        response.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON);
    }
}
