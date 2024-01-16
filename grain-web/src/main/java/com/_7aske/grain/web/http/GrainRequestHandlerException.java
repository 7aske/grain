package com._7aske.grain.web.http;

import com._7aske.grain.web.exception.HttpException;

public class GrainRequestHandlerException extends HttpException.InternalServerError {
    public GrainRequestHandlerException(String message) {
        super(message);
    }

    public GrainRequestHandlerException(Throwable cause) {
        super(cause);
    }

    public GrainRequestHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
