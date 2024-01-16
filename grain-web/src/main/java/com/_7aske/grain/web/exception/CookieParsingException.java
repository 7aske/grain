package com._7aske.grain.web.exception;

import com._7aske.grain.exception.GrainRuntimeException;

public class CookieParsingException extends GrainRuntimeException {
    public CookieParsingException() {
        super();
    }

    public CookieParsingException(String message) {
        super(message);
    }

    public CookieParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookieParsingException(Throwable cause) {
        super(cause);
    }
}
