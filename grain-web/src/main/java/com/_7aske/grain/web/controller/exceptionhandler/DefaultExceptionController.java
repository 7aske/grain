package com._7aske.grain.web.controller.exceptionhandler;

import com._7aske.grain.web.controller.annotation.ExceptionController;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.exception.HttpException;
import com._7aske.grain.web.ui.impl.ErrorPage;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.HttpStatus;

import java.io.IOException;
import java.util.Objects;

import static com._7aske.grain.web.http.HttpHeaders.ACCEPT;
import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@ExceptionController
public class DefaultExceptionController {

    @Order(255)
    @ExceptionHandler(HttpException.class)
    public Object handle(HttpException ex, HttpRequest request, HttpResponse response) throws IOException {
        if (!response.isCommitted())
            response.reset();
        response.setStatus(ex.getStatus());

        if (Objects.equals(request.getHeader(ACCEPT), ContentType.APPLICATION_JSON) ||
            Objects.equals(request.getHeader(CONTENT_TYPE), ContentType.APPLICATION_JSON)) {
            return ErrorResponse.builder()
                    .error(ex.getMessage())
                    .status(ex.getStatus())
                    .path(request.getPath())
                    .build();

        } else {
            return ErrorPage.forException(ex)
                    .path(request.getPath())
                    .build();
        }
    }

    @Order(256)
    @ExceptionHandler(Exception.class)
    public Object handle(Exception ex, HttpRequest request, HttpResponse response) throws IOException {
        if (!response.isCommitted())
            response.reset();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        if (Objects.equals(request.getHeader(ACCEPT), ContentType.APPLICATION_JSON) ||
            Objects.equals(request.getHeader(CONTENT_TYPE), ContentType.APPLICATION_JSON)) {
            return ErrorResponse.builder()
                    .error(ex.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .path(request.getPath())
                    .build();
        } else {
            return ErrorPage.builder()
                    .exception(ex)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .path(request.getPath())
                    .build();
        }
    }
}
