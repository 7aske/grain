package com._7aske.grain.web.controller.exception;

import com._7aske.grain.web.exception.HttpException;
import com._7aske.grain.util.formatter.StringFormat;

public class NoValidConverterException extends HttpException.InternalServerError {
    public NoValidConverterException(Class<?> clazz) {
        super(StringFormat.format("No valid converter found for class {}", clazz));
    }
}
