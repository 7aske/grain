package com._7aske.grain.web.controller.exceptionhandler;

import com._7aske.grain.core.component.ExceptionController;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.ui.impl.ErrorPage;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.HttpStatus;
import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.http.codec.json.nodes.JsonNode;
import com._7aske.grain.web.http.codec.json.nodes.JsonObjectNode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static com._7aske.grain.web.http.HttpHeaders.ACCEPT;
import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@ExceptionController
public class DefaultExceptionController {
    private final JsonMapper jsonMapper;

    public DefaultExceptionController(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Order(255)
    @ExceptionHandler(HttpException.class)
    public void handle(HttpException ex, HttpRequest request, HttpResponse response) throws IOException {
        response.reset();
        response.setStatus(ex.getStatus());
        if (Objects.equals(request.getHeader(ACCEPT), ContentType.APPLICATION_JSON) ||
            Objects.equals(request.getHeader(CONTENT_TYPE), ContentType.APPLICATION_JSON)) {
            response.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON);
            try(OutputStream outputStream = response.getOutputStream()) {
                JsonNode jsonObject = getErrorJsonResponse(ex);
                jsonMapper.writeValue(jsonObject, outputStream);
            }
        } else {
            response.setHeader(CONTENT_TYPE, ContentType.TEXT_HTML);
            response.getOutputStream().write(ErrorPage.getDefault(ex, ex.getStatus(), request.getPath()).getBytes());
        }
    }

    @Order(256)
    @ExceptionHandler(Exception.class)
    public void handle(Exception ex, HttpRequest request, HttpResponse response) throws IOException {
        response.reset();

        if (Objects.equals(request.getHeader(ACCEPT), ContentType.APPLICATION_JSON) ||
            Objects.equals(request.getHeader(CONTENT_TYPE), ContentType.APPLICATION_JSON)) {
            response.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON);
            try(OutputStream outputStream = response.getOutputStream()) {
                JsonNode jsonObject = getErrorJsonResponse(ex, request);
                jsonMapper.writeValue(jsonObject, outputStream);
            }
        } else {
            response.setHeader(CONTENT_TYPE, ContentType.TEXT_HTML);
            response.getOutputStream().write(ErrorPage.getDefault(ex, HttpStatus.valueOf(response.getStatus()), request.getPath()).getBytes());
        }
    }

    private JsonNode getErrorJsonResponse(Throwable ex, HttpRequest request) {
        JsonObjectNode jsonObject = new JsonObjectNode();
        jsonObject.putString("error", ex.getMessage());
        jsonObject.putString("status", HttpStatus.INTERNAL_SERVER_ERROR.getReason());
        jsonObject.putNumber("code", HttpStatus.INTERNAL_SERVER_ERROR.getValue());
        jsonObject.putString("path", request.getPath());
        return jsonObject;
    }

    private JsonNode getErrorJsonResponse(HttpException ex) {
        JsonObjectNode jsonObject = new JsonObjectNode();
        jsonObject.putString("error", ex.getMessage());
        jsonObject.putString("status", ex.getStatus().getReason());
        jsonObject.putNumber("code", ex.getStatus().getValue());
        jsonObject.putString("path", ex.getPath());
        return jsonObject;
    }
}
