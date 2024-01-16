package com._7aske.grain.web.controller.response;

import com._7aske.grain.util.By;
import com._7aske.grain.core.component.Grain;

import java.util.List;
import java.util.Optional;

@Grain
public class ResponseWriterRegistry {
    private final List<ResponseWriter<?>> responseWriters;

    public ResponseWriterRegistry(List<ResponseWriter<?>> responseWriters) {
        this.responseWriters = responseWriters;
    }


    public Optional<ResponseWriter<?>> getWriter(Object object) {
        return responseWriters.stream()
                .filter(writer -> writer.supports(object))
                .min(By::objectOrder);
    }

    public List<ResponseWriter<?>> getResponseWriters() {
        return responseWriters;
    }

    public void addResponseWriter(ResponseWriter<?> responseWriter) {
        responseWriters.add(responseWriter);
    }

    public void removeResponseWriter(ResponseWriter<?> responseWriter) {
        responseWriters.remove(responseWriter);
    }
}
