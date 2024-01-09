package com._7aske.grain.web.controller;

import com._7aske.grain.web.controller.annotation.ResponseStatus;
import com._7aske.grain.web.http.HttpStatus;

public class ResponseStatusResolver {
    private ResponseStatusResolver() {}

    public static int resolveStatus(ResponseStatus responseStatus) {
        if (responseStatus == null) {
            return HttpStatus.OK.getValue();
        }

        if (responseStatus.code() != ResponseStatus.NO_VALUE) {
            return responseStatus.code();
        }

        return responseStatus.value().getValue();
    }
}
