package com._7aske.grain.web.exception;

import com._7aske.grain.exception.GrainRuntimeException;

public class HttpParsingException extends GrainRuntimeException {
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
}
