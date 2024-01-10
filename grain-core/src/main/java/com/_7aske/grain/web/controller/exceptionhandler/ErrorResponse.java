package com._7aske.grain.web.controller.exceptionhandler;

import com._7aske.grain.web.http.HttpStatus;

public record ErrorResponse(String error, String status, int code, String path) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String error;
        private String status;
        private int code;
        private String path;

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status.getReason();
            this.code = status.getValue();
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(error, status, code, path);
        }
    }
}
