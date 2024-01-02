package com._7aske.grain.exception.http;

public class HttpParsingException extends RuntimeException {
    public HttpParsingException() {
        super();
    }

    public HttpParsingException(String message) {
        super(message);
    }

    public HttpParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpParsingException(Throwable cause) {
        super(cause);
    }

    protected HttpParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
