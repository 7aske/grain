package com._7aske.grain.web.controller.exception;

import com._7aske.grain.exception.http.HttpException;

public class RequestParameterRequiredException extends HttpException.BadRequest {
    public RequestParameterRequiredException(String value) {
        super("Request parameter '" + value + "' is required");
    }
}
