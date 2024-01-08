package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.http.codec.json.nodes.JsonNode;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
public class JsonNodeResponseWriter extends AbstractResponseWriterSupport<JsonNode> {
    private final JsonMapper jsonMapper;

    public JsonNodeResponseWriter(JsonMapper jsonMapper) {
        super(JsonNode.class);
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void writeInternal(JsonNode returnValue, HttpResponse response, HttpRequest request, RequestHandler handler) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            jsonMapper.writeValue(returnValue, outputStream);
        }
        response.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON);
    }
}
