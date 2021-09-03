package com._7aske.grain.exception.http;

import com._7aske.grain.http.HttpStatus;

import static com._7aske.grain.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static com._7aske.grain.http.HttpStatus.NOT_FOUND;

public class HttpException extends RuntimeException {
	private final HttpStatus status;
	public HttpException(HttpStatus status) {
		super(status.getReason());
		this.status = status;
	}

	public HttpException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpException(HttpStatus status, Throwable cause) {
		super(status.getReason(), cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static final class NotFound extends HttpException {
		public NotFound() {
			super(NOT_FOUND);
		}
	}

	public static final class InternalServerError extends HttpException {
		public InternalServerError(Throwable cause) {
			super(INTERNAL_SERVER_ERROR, cause);
		}
	}
}
