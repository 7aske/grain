package com._7aske.grain.exception.http;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.http.HttpStatus;

import static com._7aske.grain.web.http.HttpStatus.*;

public abstract class HttpException extends GrainRuntimeException {
	private final HttpStatus status;
	private final String path;

	protected HttpException(HttpStatus status, String path) {
		this.status = status;
		this.path = path;
	}

	protected HttpException(Throwable cause, HttpStatus status) {
		super(cause);
		this.status = status;
		this.path = null;
	}

	protected HttpException(Throwable cause, HttpStatus status, String path) {
		super(cause);
		this.status = status;
		this.path = path;
	}

	protected HttpException(String message, HttpStatus status, String path) {
		super(message);
		this.status = status;
		this.path = path;
	}

	protected HttpException(String message, Throwable cause, HttpStatus status, String path) {
		super(message, cause);
		this.status = status;
		this.path = path;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getPath() {
		return path;
	}

	public static final class BadRequest extends HttpException {
		public BadRequest(String path) {
			super(BAD_REQUEST, path);
		}

		public BadRequest(Throwable cause, String path) {
			super(cause, BAD_REQUEST, path);
		}

		public BadRequest(String message, String path) {
			super(message, BAD_REQUEST, path);
		}

		public BadRequest(String message, Throwable cause, String path) {
			super(message, cause, BAD_REQUEST, path);
		}

		public BadRequest(Throwable cause) {
			super(cause, BAD_REQUEST);
		}
	}

	public static final class Unauthorized extends HttpException {
		public Unauthorized(String path) {
			super(UNAUTHORIZED, path);
		}

		public Unauthorized(Throwable cause, String path) {
			super(cause, UNAUTHORIZED, path);
		}

		public Unauthorized(String message, String path) {
			super(message, UNAUTHORIZED, path);
		}

		public Unauthorized(String message, Throwable cause, String path) {
			super(message, cause, UNAUTHORIZED, path);
		}

		public Unauthorized(Throwable cause) {
			super(cause, UNAUTHORIZED);
		}
	}

	public static final class PaymentRequired extends HttpException {
		public PaymentRequired(String path) {
			super(PAYMENT_REQUIRED, path);
		}

		public PaymentRequired(Throwable cause, String path) {
			super(cause, PAYMENT_REQUIRED, path);
		}

		public PaymentRequired(String message, String path) {
			super(message, PAYMENT_REQUIRED, path);
		}

		public PaymentRequired(String message, Throwable cause, String path) {
			super(message, cause, PAYMENT_REQUIRED, path);
		}

		public PaymentRequired(Throwable cause) {
			super(cause, PAYMENT_REQUIRED);
		}
	}

	public static final class Forbidden extends HttpException {
		public Forbidden(String path) {
			super(FORBIDDEN, path);
		}

		public Forbidden(Throwable cause, String path) {
			super(cause, FORBIDDEN, path);
		}

		public Forbidden(String message, String path) {
			super(message, FORBIDDEN, path);
		}

		public Forbidden(String message, Throwable cause, String path) {
			super(message, cause, FORBIDDEN, path);
		}

		public Forbidden(Throwable cause) {
			super(cause, FORBIDDEN);
		}
	}

	public static final class NotFound extends HttpException {
		public NotFound(String path) {
			super(NOT_FOUND, path);
		}

		public NotFound(Throwable cause, String path) {
			super(cause, NOT_FOUND, path);
		}

		public NotFound(String message, String path) {
			super(message, NOT_FOUND, path);
		}

		public NotFound(String message, Throwable cause, String path) {
			super(message, cause, NOT_FOUND, path);
		}

		public NotFound(Throwable cause) {
			super(cause, NOT_FOUND);
		}
	}

	public static final class MethodNotAllowed extends HttpException {
		public MethodNotAllowed(String path) {
			super(METHOD_NOT_ALLOWED, path);
		}

		public MethodNotAllowed(Throwable cause, String path) {
			super(cause, METHOD_NOT_ALLOWED, path);
		}

		public MethodNotAllowed(String message, String path) {
			super(message, METHOD_NOT_ALLOWED, path);
		}

		public MethodNotAllowed(String message, Throwable cause, String path) {
			super(message, cause, METHOD_NOT_ALLOWED, path);
		}

		public MethodNotAllowed(Throwable throwable) {
			super(throwable, METHOD_NOT_ALLOWED);
		}
	}

	public static final class NotAcceptable extends HttpException {
		public NotAcceptable(String path) {
			super(NOT_ACCEPTABLE, path);
		}

		public NotAcceptable(Throwable cause, String path) {
			super(cause, NOT_ACCEPTABLE, path);
		}

		public NotAcceptable(String message, String path) {
			super(message, NOT_ACCEPTABLE, path);
		}

		public NotAcceptable(String message, Throwable cause, String path) {
			super(message, cause, NOT_ACCEPTABLE, path);
		}

		public NotAcceptable(Throwable cause) {
			super(cause, NOT_ACCEPTABLE);
		}
	}

	public static final class ProxyAuthenticationRequired extends HttpException {
		public ProxyAuthenticationRequired(String path) {
			super(PROXY_AUTHENTICATION_REQUIRED, path);
		}

		public ProxyAuthenticationRequired(Throwable cause, String path) {
			super(cause, PROXY_AUTHENTICATION_REQUIRED, path);
		}

		public ProxyAuthenticationRequired(String message, String path) {
			super(message, PROXY_AUTHENTICATION_REQUIRED, path);
		}

		public ProxyAuthenticationRequired(String message, Throwable cause, String path) {
			super(message, cause, PROXY_AUTHENTICATION_REQUIRED, path);
		}

		public ProxyAuthenticationRequired(Throwable cause) {
			super(cause, PROXY_AUTHENTICATION_REQUIRED);
		}
	}

	public static final class RequestTimeout extends HttpException {
		public RequestTimeout(String path) {
			super(REQUEST_TIMEOUT, path);
		}

		public RequestTimeout(Throwable cause, String path) {
			super(cause, REQUEST_TIMEOUT, path);
		}

		public RequestTimeout(String message, String path) {
			super(message, REQUEST_TIMEOUT, path);
		}

		public RequestTimeout(String message, Throwable cause, String path) {
			super(message, cause, REQUEST_TIMEOUT, path);
		}

		public RequestTimeout(Throwable cause) {
			super(cause, REQUEST_TIMEOUT);
		}
	}

	public static final class Conflict extends HttpException {
		public Conflict(String path) {
			super(CONFLICT, path);
		}

		public Conflict(Throwable cause, String path) {
			super(cause, CONFLICT, path);
		}

		public Conflict(String message, String path) {
			super(message, CONFLICT, path);
		}

		public Conflict(String message, Throwable cause, String path) {
			super(message, cause, CONFLICT, path);
		}

		public Conflict(Throwable cause) {
			super(cause, CONFLICT);
		}
	}

	public static final class Gone extends HttpException {
		public Gone(String path) {
			super(GONE, path);
		}

		public Gone(Throwable cause, String path) {
			super(cause, GONE, path);
		}

		public Gone(String message, String path) {
			super(message, GONE, path);
		}

		public Gone(String message, Throwable cause, String path) {
			super(message, cause, GONE, path);
		}

		public Gone(Throwable cause) {
			super(cause, GONE);
		}
	}

	public static final class LengthRequired extends HttpException {
		public LengthRequired(String path) {
			super(LENGTH_REQUIRED, path);
		}

		public LengthRequired(Throwable cause, String path) {
			super(cause, LENGTH_REQUIRED, path);
		}

		public LengthRequired(String message, String path) {
			super(message, LENGTH_REQUIRED, path);
		}

		public LengthRequired(String message, Throwable cause, String path) {
			super(message, cause, LENGTH_REQUIRED, path);
		}

		public LengthRequired(Throwable cause) {
			super(cause, LENGTH_REQUIRED);
		}
	}

	public static final class PreconditionFailed extends HttpException {
		public PreconditionFailed(String path) {
			super(PRECONDITION_FAILED, path);
		}

		public PreconditionFailed(Throwable cause, String path) {
			super(cause, PRECONDITION_FAILED, path);
		}

		public PreconditionFailed(String message, String path) {
			super(message, PRECONDITION_FAILED, path);
		}

		public PreconditionFailed(String message, Throwable cause, String path) {
			super(message, cause, PRECONDITION_FAILED, path);
		}

		public PreconditionFailed(Throwable cause) {
			super(cause, PRECONDITION_FAILED);
		}
	}

	public static final class PayloadTooLarge extends HttpException {
		public PayloadTooLarge(String path) {
			super(PAYLOAD_TOO_LARGE, path);
		}

		public PayloadTooLarge(Throwable cause, String path) {
			super(cause, PAYLOAD_TOO_LARGE, path);
		}

		public PayloadTooLarge(String message, String path) {
			super(message, PAYLOAD_TOO_LARGE, path);
		}

		public PayloadTooLarge(String message, Throwable cause, String path) {
			super(message, cause, PAYLOAD_TOO_LARGE, path);
		}

		public PayloadTooLarge(Throwable cause) {
			super(cause, PAYLOAD_TOO_LARGE);
		}
	}

	@Deprecated
	public static final class RequestEntityTooLarge extends HttpException {
		public RequestEntityTooLarge(String path) {
			super(REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestEntityTooLarge(Throwable cause, String path) {
			super(cause, REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestEntityTooLarge(String message, String path) {
			super(message, REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestEntityTooLarge(String message, Throwable cause, String path) {
			super(message, cause, REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestEntityTooLarge(Throwable cause) {
			super(cause, REQUEST_ENTITY_TOO_LARGE);
		}
	}

	public static final class UriTooLong extends HttpException {
		public UriTooLong(String path) {
			super(URI_TOO_LONG, path);
		}

		public UriTooLong(Throwable cause, String path) {
			super(cause, URI_TOO_LONG, path);
		}

		public UriTooLong(String message, String path) {
			super(message, URI_TOO_LONG, path);
		}

		public UriTooLong(String message, Throwable cause, String path) {
			super(message, cause, URI_TOO_LONG, path);
		}

		public UriTooLong(Throwable cause) {
			super(cause, URI_TOO_LONG);
		}
	}

	@Deprecated
	public static final class RequestUriTooLong extends HttpException {
		public RequestUriTooLong(String path) {
			super(REQUEST_URI_TOO_LONG, path);
		}

		public RequestUriTooLong(Throwable cause, String path) {
			super(cause, REQUEST_URI_TOO_LONG, path);
		}

		public RequestUriTooLong(String message, String path) {
			super(message, REQUEST_URI_TOO_LONG, path);
		}

		public RequestUriTooLong(String message, Throwable cause, String path) {
			super(message, cause, REQUEST_URI_TOO_LONG, path);
		}

		public RequestUriTooLong(Throwable cause) {
			super(cause, REQUEST_URI_TOO_LONG);
		}
	}

	public static final class UnsupportedMediaType extends HttpException {
		public UnsupportedMediaType(String path) {
			super(UNSUPPORTED_MEDIA_TYPE, path);
		}

		public UnsupportedMediaType(Throwable cause, String path) {
			super(cause, UNSUPPORTED_MEDIA_TYPE, path);
		}

		public UnsupportedMediaType(String message, String path) {
			super(message, UNSUPPORTED_MEDIA_TYPE, path);
		}

		public UnsupportedMediaType(String message, Throwable cause, String path) {
			super(message, cause, UNSUPPORTED_MEDIA_TYPE, path);
		}

		public UnsupportedMediaType(Throwable cause) {
			super(cause, UNSUPPORTED_MEDIA_TYPE);
		}
	}

	public static final class RequestedRangeNotSatisfiable extends HttpException {
		public RequestedRangeNotSatisfiable(String path) {
			super(REQUESTED_RANGE_NOT_SATISFIABLE, path);
		}

		public RequestedRangeNotSatisfiable(Throwable cause, String path) {
			super(cause, REQUESTED_RANGE_NOT_SATISFIABLE, path);
		}

		public RequestedRangeNotSatisfiable(String message, String path) {
			super(message, REQUESTED_RANGE_NOT_SATISFIABLE, path);
		}

		public RequestedRangeNotSatisfiable(String message, Throwable cause, String path) {
			super(message, cause, REQUESTED_RANGE_NOT_SATISFIABLE, path);
		}

		public RequestedRangeNotSatisfiable(Throwable cause) {
			super(cause, REQUESTED_RANGE_NOT_SATISFIABLE);
		}
	}

	public static final class ExpectationFailed extends HttpException {
		public ExpectationFailed(String path) {
			super(EXPECTATION_FAILED, path);
		}

		public ExpectationFailed(Throwable cause, String path) {
			super(cause, EXPECTATION_FAILED, path);
		}

		public ExpectationFailed(String message, String path) {
			super(message, EXPECTATION_FAILED, path);
		}

		public ExpectationFailed(String message, Throwable cause, String path) {
			super(message, cause, EXPECTATION_FAILED, path);
		}

		public ExpectationFailed(Throwable cause) {
			super(cause, EXPECTATION_FAILED);
		}
	}

	public static final class IAmATeapot extends HttpException {
		public IAmATeapot(String path) {
			super(I_AM_A_TEAPOT, path);
		}

		public IAmATeapot(Throwable cause, String path) {
			super(cause, I_AM_A_TEAPOT, path);
		}

		public IAmATeapot(String message, String path) {
			super(message, I_AM_A_TEAPOT, path);
		}

		public IAmATeapot(String message, Throwable cause, String path) {
			super(message, cause, I_AM_A_TEAPOT, path);
		}

		public IAmATeapot(Throwable cause) {
			super(cause, I_AM_A_TEAPOT);
		}
	}

	@Deprecated
	public static final class InsufficientSpaceOnResource extends HttpException {
		public InsufficientSpaceOnResource(String path) {
			super(INSUFFICIENT_SPACE_ON_RESOURCE, path);
		}

		public InsufficientSpaceOnResource(Throwable cause, String path) {
			super(cause, INSUFFICIENT_SPACE_ON_RESOURCE, path);
		}

		public InsufficientSpaceOnResource(String message, String path) {
			super(message, INSUFFICIENT_SPACE_ON_RESOURCE, path);
		}

		public InsufficientSpaceOnResource(String message, Throwable cause, String path) {
			super(message, cause, INSUFFICIENT_SPACE_ON_RESOURCE, path);
		}

		public InsufficientSpaceOnResource(Throwable cause) {
			super(cause, INSUFFICIENT_SPACE_ON_RESOURCE);
		}
	}

	@Deprecated
	public static final class MethodFailure extends HttpException {
		public MethodFailure(String path) {
			super(METHOD_FAILURE, path);
		}

		public MethodFailure(Throwable cause, String path) {
			super(cause, METHOD_FAILURE, path);
		}

		public MethodFailure(String message, String path) {
			super(message, METHOD_FAILURE, path);
		}

		public MethodFailure(String message, Throwable cause, String path) {
			super(message, cause, METHOD_FAILURE, path);
		}

		public MethodFailure(Throwable cause) {
			super(cause, METHOD_FAILURE);
		}
	}

	@Deprecated
	public static final class DestinationLocked extends HttpException {
		public DestinationLocked(String path) {
			super(DESTINATION_LOCKED, path);
		}

		public DestinationLocked(Throwable cause, String path) {
			super(cause, DESTINATION_LOCKED, path);
		}

		public DestinationLocked(String message, String path) {
			super(message, DESTINATION_LOCKED, path);
		}

		public DestinationLocked(String message, Throwable cause, String path) {
			super(message, cause, DESTINATION_LOCKED, path);
		}

		public DestinationLocked(Throwable cause) {
			super(cause, DESTINATION_LOCKED);
		}
	}

	public static final class UnprocessableEntity extends HttpException {
		public UnprocessableEntity(String path) {
			super(UNPROCESSABLE_ENTITY, path);
		}

		public UnprocessableEntity(Throwable cause, String path) {
			super(cause, UNPROCESSABLE_ENTITY, path);
		}

		public UnprocessableEntity(String message, String path) {
			super(message, UNPROCESSABLE_ENTITY, path);
		}

		public UnprocessableEntity(String message, Throwable cause, String path) {
			super(message, cause, UNPROCESSABLE_ENTITY, path);
		}

		public UnprocessableEntity(Throwable cause) {
			super(cause, UNPROCESSABLE_ENTITY);
		}
	}

	public static final class Locked extends HttpException {
		public Locked(String path) {
			super(LOCKED, path);
		}

		public Locked(Throwable cause, String path) {
			super(cause, LOCKED, path);
		}

		public Locked(String message, String path) {
			super(message, LOCKED, path);
		}

		public Locked(String message, Throwable cause, String path) {
			super(message, cause, LOCKED, path);
		}

		public Locked(Throwable cause) {
			super(cause, LOCKED);
		}
	}

	public static final class FailedDependency extends HttpException {
		public FailedDependency(String path) {
			super(FAILED_DEPENDENCY, path);
		}

		public FailedDependency(Throwable cause, String path) {
			super(cause, FAILED_DEPENDENCY, path);
		}

		public FailedDependency(String message, String path) {
			super(message, FAILED_DEPENDENCY, path);
		}

		public FailedDependency(String message, Throwable cause, String path) {
			super(message, cause, FAILED_DEPENDENCY, path);
		}

		public FailedDependency(Throwable cause) {
			super(cause, FAILED_DEPENDENCY);
		}
	}

	public static final class TooEarly extends HttpException {
		public TooEarly(String path) {
			super(TOO_EARLY, path);
		}

		public TooEarly(Throwable cause, String path) {
			super(cause, TOO_EARLY, path);
		}

		public TooEarly(String message, String path) {
			super(message, TOO_EARLY, path);
		}

		public TooEarly(String message, Throwable cause, String path) {
			super(message, cause, TOO_EARLY, path);
		}

		public TooEarly(Throwable cause) {
			super(cause, TOO_EARLY);
		}
	}

	public static final class UpgradeRequired extends HttpException {
		public UpgradeRequired(String path) {
			super(UPGRADE_REQUIRED, path);
		}

		public UpgradeRequired(Throwable cause, String path) {
			super(cause, UPGRADE_REQUIRED, path);
		}

		public UpgradeRequired(String message, String path) {
			super(message, UPGRADE_REQUIRED, path);
		}

		public UpgradeRequired(String message, Throwable cause, String path) {
			super(message, cause, UPGRADE_REQUIRED, path);
		}

		public UpgradeRequired(Throwable cause) {
			super(cause, UPGRADE_REQUIRED);
		}
	}

	public static final class PreconditionRequired extends HttpException {
		public PreconditionRequired(String path) {
			super(PRECONDITION_REQUIRED, path);
		}

		public PreconditionRequired(Throwable cause, String path) {
			super(cause, PRECONDITION_REQUIRED, path);
		}

		public PreconditionRequired(String message, String path) {
			super(message, PRECONDITION_REQUIRED, path);
		}

		public PreconditionRequired(String message, Throwable cause, String path) {
			super(message, cause, PRECONDITION_REQUIRED, path);
		}

		public PreconditionRequired(Throwable cause) {
			super(cause, PRECONDITION_REQUIRED);
		}
	}

	public static final class TooManyRequests extends HttpException {
		public TooManyRequests(String path) {
			super(TOO_MANY_REQUESTS, path);
		}

		public TooManyRequests(Throwable cause, String path) {
			super(cause, TOO_MANY_REQUESTS, path);
		}

		public TooManyRequests(String message, String path) {
			super(message, TOO_MANY_REQUESTS, path);
		}

		public TooManyRequests(String message, Throwable cause, String path) {
			super(message, cause, TOO_MANY_REQUESTS, path);
		}

		public TooManyRequests(Throwable cause) {
			super(cause, TOO_MANY_REQUESTS);
		}
	}

	@Deprecated
	public static final class RequestHeaderFieldsTooLarge extends HttpException {
		public RequestHeaderFieldsTooLarge(String path) {
			super(REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestHeaderFieldsTooLarge(Throwable cause, String path) {
			super(cause, REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestHeaderFieldsTooLarge(String message, String path) {
			super(message, REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestHeaderFieldsTooLarge(String message, Throwable cause, String path) {
			super(message, cause, REQUEST_ENTITY_TOO_LARGE, path);
		}

		public RequestHeaderFieldsTooLarge(Throwable cause) {
			super(cause, REQUEST_ENTITY_TOO_LARGE);
		}
	}

	public static final class UnavailableForLegalReasons extends HttpException {
		public UnavailableForLegalReasons(String path) {
			super(UNAVAILABLE_FOR_LEGAL_REASONS, path);
		}

		public UnavailableForLegalReasons(Throwable cause, String path) {
			super(cause, UNAVAILABLE_FOR_LEGAL_REASONS, path);
		}

		public UnavailableForLegalReasons(String message, String path) {
			super(message, UNAVAILABLE_FOR_LEGAL_REASONS, path);
		}

		public UnavailableForLegalReasons(String message, Throwable cause, String path) {
			super(message, cause, UNAVAILABLE_FOR_LEGAL_REASONS, path);
		}

		public UnavailableForLegalReasons(Throwable cause) {
			super(cause, UNAVAILABLE_FOR_LEGAL_REASONS);
		}
	}

	public static final class InternalServerError extends HttpException {
		public InternalServerError(String path) {
			super(INTERNAL_SERVER_ERROR, path);
		}

		public InternalServerError(Throwable cause, String path) {
			super(cause, INTERNAL_SERVER_ERROR, path);
		}

		public InternalServerError(String message, String path) {
			super(message, INTERNAL_SERVER_ERROR, path);
		}

		public InternalServerError(String message, Throwable cause, String path) {
			super(message, cause, INTERNAL_SERVER_ERROR, path);
		}

		public InternalServerError(Throwable cause) {
			super(cause, INTERNAL_SERVER_ERROR);
		}
	}

	public static final class NotImplemented extends HttpException {
		public NotImplemented(String path) {
			super(NOT_IMPLEMENTED, path);
		}

		public NotImplemented(Throwable cause, String path) {
			super(cause, NOT_IMPLEMENTED, path);
		}

		public NotImplemented(String message, String path) {
			super(message, NOT_IMPLEMENTED, path);
		}

		public NotImplemented(String message, Throwable cause, String path) {
			super(message, cause, NOT_IMPLEMENTED, path);
		}

		public NotImplemented(Throwable cause) {
			super(cause, NOT_IMPLEMENTED);
		}
	}

	public static final class BadGateway extends HttpException {
		public BadGateway(String path) {
			super(BAD_GATEWAY, path);
		}

		public BadGateway(Throwable cause, String path) {
			super(cause, BAD_GATEWAY, path);
		}

		public BadGateway(String message, String path) {
			super(message, BAD_GATEWAY, path);
		}

		public BadGateway(String message, Throwable cause, String path) {
			super(message, cause, BAD_GATEWAY, path);
		}

		public BadGateway(Throwable cause) {
			super(cause, BAD_GATEWAY);
		}
	}

	public static final class ServiceUnavailable extends HttpException {
		public ServiceUnavailable(String path) {
			super(SERVICE_UNAVAILABLE, path);
		}

		public ServiceUnavailable(Throwable cause, String path) {
			super(cause, SERVICE_UNAVAILABLE, path);
		}

		public ServiceUnavailable(String message, String path) {
			super(message, SERVICE_UNAVAILABLE, path);
		}

		public ServiceUnavailable(String message, Throwable cause, String path) {
			super(message, cause, SERVICE_UNAVAILABLE, path);
		}

		public ServiceUnavailable(Throwable cause) {
			super(cause, SERVICE_UNAVAILABLE);
		}
	}

	public static final class GatewayTimeout extends HttpException {
		public GatewayTimeout(String path) {
			super(GATEWAY_TIMEOUT, path);
		}

		public GatewayTimeout(Throwable cause, String path) {
			super(cause, GATEWAY_TIMEOUT, path);
		}

		public GatewayTimeout(String message, String path) {
			super(message, GATEWAY_TIMEOUT, path);
		}

		public GatewayTimeout(String message, Throwable cause, String path) {
			super(message, cause, GATEWAY_TIMEOUT, path);
		}

		public GatewayTimeout(Throwable cause) {
			super(cause, GATEWAY_TIMEOUT);
		}
	}

	public static final class HttpVersionNotSupported extends HttpException {
		public HttpVersionNotSupported(String path) {
			super(HTTP_VERSION_NOT_SUPPORTED, path);
		}

		public HttpVersionNotSupported(Throwable cause, String path) {
			super(cause, HTTP_VERSION_NOT_SUPPORTED, path);
		}

		public HttpVersionNotSupported(String message, String path) {
			super(message, HTTP_VERSION_NOT_SUPPORTED, path);
		}

		public HttpVersionNotSupported(String message, Throwable cause, String path) {
			super(message, cause, HTTP_VERSION_NOT_SUPPORTED, path);
		}

		public HttpVersionNotSupported(Throwable cause) {
			super(cause, HTTP_VERSION_NOT_SUPPORTED);
		}
	}

	public static final class VariantAlsoNegotiates extends HttpException {
		public VariantAlsoNegotiates(String path) {
			super(VARIANT_ALSO_NEGOTIATES, path);
		}

		public VariantAlsoNegotiates(Throwable cause, String path) {
			super(cause, VARIANT_ALSO_NEGOTIATES, path);
		}

		public VariantAlsoNegotiates(String message, String path) {
			super(message, VARIANT_ALSO_NEGOTIATES, path);
		}

		public VariantAlsoNegotiates(String message, Throwable cause, String path) {
			super(message, cause, VARIANT_ALSO_NEGOTIATES, path);
		}

		public VariantAlsoNegotiates(Throwable cause) {
			super(cause, VARIANT_ALSO_NEGOTIATES);
		}
	}

	public static final class InsufficientStorage extends HttpException {
		public InsufficientStorage(String path) {
			super(INSUFFICIENT_STORAGE, path);
		}

		public InsufficientStorage(Throwable cause, String path) {
			super(cause, INSUFFICIENT_STORAGE, path);
		}

		public InsufficientStorage(String message, String path) {
			super(message, INSUFFICIENT_STORAGE, path);
		}

		public InsufficientStorage(String message, Throwable cause, String path) {
			super(message, cause, INSUFFICIENT_STORAGE, path);
		}

		public InsufficientStorage(Throwable cause) {
			super(cause, INSUFFICIENT_STORAGE);
		}
	}

	public static final class LoopDetected extends HttpException {
		public LoopDetected(String path) {
			super(LOOP_DETECTED, path);
		}

		public LoopDetected(Throwable cause, String path) {
			super(cause, LOOP_DETECTED, path);
		}

		public LoopDetected(String message, String path) {
			super(message, LOOP_DETECTED, path);
		}

		public LoopDetected(String message, Throwable cause, String path) {
			super(message, cause, LOOP_DETECTED, path);
		}

		public LoopDetected(Throwable cause) {
			super(cause, LOOP_DETECTED);
		}
	}

	public static final class NotExtended extends HttpException {
		public NotExtended(String path) {
			super(NOT_EXTENDED, path);
		}

		public NotExtended(Throwable cause, String path) {
			super(cause, NOT_EXTENDED, path);
		}

		public NotExtended(String message, String path) {
			super(message, NOT_EXTENDED, path);
		}

		public NotExtended(String message, Throwable cause, String path) {
			super(message, cause, NOT_EXTENDED, path);
		}

		public NotExtended(Throwable cause) {
			super(cause, NOT_EXTENDED);
		}
	}

	public static final class NetworkAuthenticationRequired extends HttpException {
		public NetworkAuthenticationRequired(String path) {
			super(NETWORK_AUTHENTICATION_REQUIRED, path);
		}

		public NetworkAuthenticationRequired(Throwable cause, String path) {
			super(cause, NETWORK_AUTHENTICATION_REQUIRED, path);
		}

		public NetworkAuthenticationRequired(String message, String path) {
			super(message, NETWORK_AUTHENTICATION_REQUIRED, path);
		}

		public NetworkAuthenticationRequired(String message, Throwable cause, String path) {
			super(message, cause, NETWORK_AUTHENTICATION_REQUIRED, path);
		}

		public NetworkAuthenticationRequired(Throwable cause) {
			super(cause, NETWORK_AUTHENTICATION_REQUIRED);
		}
	}
}
