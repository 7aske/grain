package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.http.codec.json.JsonResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Grain
public class JsonResponseWriter extends AbstractResponseWriterSupport<JsonResponse> {
    private final JsonMapper jsonMapper;

    public JsonResponseWriter(JsonMapper jsonMapper) {
        super(JsonResponse.class);
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void writeInternal(JsonResponse returnValue, HttpRequest request, HttpResponse response, RequestHandler handler) throws IOException {
        response.setStatus(((JsonResponse<?>) returnValue).getStatus().getValue());
        for (Map.Entry<String, String> entry : ((JsonResponse<?>) returnValue).getHeaders().entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }

        try (OutputStream outputStream = response.getOutputStream()) {
            jsonMapper.writeValue(((JsonResponse<?>) returnValue).getBody(), outputStream);
        }
    }
}
