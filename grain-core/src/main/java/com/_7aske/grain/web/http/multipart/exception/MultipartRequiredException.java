package com._7aske.grain.web.http.multipart.exception;

import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.web.controller.annotation.RequestPart;
import com._7aske.grain.web.http.HttpRequest;

public class MultipartRequiredException extends HttpException.BadRequest {
    public MultipartRequiredException(RequestPart requestPart) {
        super("Multipart part '" + requestPart.value() + "' is required");
    }
}
