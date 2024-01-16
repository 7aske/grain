package com._7aske.grain.web.exception;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.http.HttpStatus;

import static com._7aske.grain.web.http.HttpStatus.*;

@SuppressWarnings("unused")
public abstract class HttpException extends GrainRuntimeException {
	private final HttpStatus status;

	protected HttpException(HttpStatus status) {
		this.status = status;
	}

	protected HttpException(Throwable cause, HttpStatus status) {
		super(cause);
		this.status = status;
	}

	protected HttpException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	protected HttpException(String message, Throwable cause, HttpStatus status) {
		super(message, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static class BadRequest extends HttpException {
		public BadRequest() {
			super(BAD_REQUEST);
		}

		public BadRequest(String message) {
			super(message, BAD_REQUEST);
		}

		public BadRequest(String message, Throwable cause) {
			super(message, cause, BAD_REQUEST);
		}

		public BadRequest(Throwable cause) {
			super(cause, BAD_REQUEST);
		}
	}

	public static class Unauthorized extends HttpException {
		public Unauthorized() {
			super(UNAUTHORIZED);
		}

		public Unauthorized(String message) {
			super(message, UNAUTHORIZED);
		}

		public Unauthorized(String message, Throwable cause) {
			super(message, cause, UNAUTHORIZED);
		}

		public Unauthorized(Throwable cause) {
			super(cause, UNAUTHORIZED);
		}
	}

	public static class PaymentRequired extends HttpException {
		public PaymentRequired() {
			super(PAYMENT_REQUIRED);
		}

		public PaymentRequired(String message) {
			super(message, PAYMENT_REQUIRED);
		}

		public PaymentRequired(String message, Throwable cause) {
			super(message, cause, PAYMENT_REQUIRED);
		}

		public PaymentRequired(Throwable cause) {
			super(cause, PAYMENT_REQUIRED);
		}
	}

	public static class Forbidden extends HttpException {
		public Forbidden() {
			super(FORBIDDEN);
		}

		public Forbidden(String message) {
			super(message, FORBIDDEN);
		}

		public Forbidden(String message, Throwable cause) {
			super(message, cause, FORBIDDEN);
		}

		public Forbidden(Throwable cause) {
			super(cause, FORBIDDEN);
		}
	}

	public static class NotFound extends HttpException {
		public NotFound() {
			super(NOT_FOUND);
		}

		public NotFound(String message) {
			super(message, NOT_FOUND);
		}

		public NotFound(String message, Throwable cause) {
			super(message, cause, NOT_FOUND);
		}

		public NotFound(Throwable cause) {
			super(cause, NOT_FOUND);
		}
	}

	public static class MethodNotAllowed extends HttpException {
		public MethodNotAllowed() {
			super(METHOD_NOT_ALLOWED);
		}

		public MethodNotAllowed(String message) {
			super(message, METHOD_NOT_ALLOWED);
		}

		public MethodNotAllowed(String message, Throwable cause) {
			super(message, cause, METHOD_NOT_ALLOWED);
		}

		public MethodNotAllowed(Throwable throwable) {
			super(throwable, METHOD_NOT_ALLOWED);
		}
	}

	public static class NotAcceptable extends HttpException {
		public NotAcceptable() {
			super(NOT_ACCEPTABLE);
		}

		public NotAcceptable(String message) {
			super(message, NOT_ACCEPTABLE);
		}

		public NotAcceptable(String message, Throwable cause) {
			super(message, cause, NOT_ACCEPTABLE);
		}

		public NotAcceptable(Throwable cause) {
			super(cause, NOT_ACCEPTABLE);
		}
	}

	public static class ProxyAuthenticationRequired extends HttpException {
		public ProxyAuthenticationRequired() {
			super(PROXY_AUTHENTICATION_REQUIRED);
		}

		public ProxyAuthenticationRequired(String message) {
			super(message, PROXY_AUTHENTICATION_REQUIRED);
		}

		public ProxyAuthenticationRequired(String message, Throwable cause) {
			super(message, cause, PROXY_AUTHENTICATION_REQUIRED);
		}

		public ProxyAuthenticationRequired(Throwable cause) {
			super(cause, PROXY_AUTHENTICATION_REQUIRED);
		}
	}

	public static class RequestTimeout extends HttpException {
		public RequestTimeout() {
			super(REQUEST_TIMEOUT);
		}

		public RequestTimeout(String message) {
			super(message, REQUEST_TIMEOUT);
		}

		public RequestTimeout(String message, Throwable cause) {
			super(message, cause, REQUEST_TIMEOUT);
		}

		public RequestTimeout(Throwable cause) {
			super(cause, REQUEST_TIMEOUT);
		}
	}

	public static class Conflict extends HttpException {
		public Conflict() {
			super(CONFLICT);
		}

		public Conflict(String message) {
			super(message, CONFLICT);
		}

		public Conflict(String message, Throwable cause) {
			super(message, cause, CONFLICT);
		}

		public Conflict(Throwable cause) {
			super(cause, CONFLICT);
		}
	}

	public static class Gone extends HttpException {
		public Gone() {
			super(GONE);
		}

		public Gone(String message) {
			super(message, GONE);
		}

		public Gone(String message, Throwable cause) {
			super(message, cause, GONE);
		}

		public Gone(Throwable cause) {
			super(cause, GONE);
		}
	}

	public static class LengthRequired extends HttpException {
		public LengthRequired() {
			super(LENGTH_REQUIRED);
		}

		public LengthRequired(String message) {
			super(message, LENGTH_REQUIRED);
		}

		public LengthRequired(String message, Throwable cause) {
			super(message, cause, LENGTH_REQUIRED);
		}

		public LengthRequired(Throwable cause) {
			super(cause, LENGTH_REQUIRED);
		}
	}

	public static class PreconditionFailed extends HttpException {
		public PreconditionFailed() {
			super(PRECONDITION_FAILED);
		}

		public PreconditionFailed(String message) {
			super(message, PRECONDITION_FAILED);
		}

		public PreconditionFailed(String message, Throwable cause) {
			super(message, cause, PRECONDITION_FAILED);
		}

		public PreconditionFailed(Throwable cause) {
			super(cause, PRECONDITION_FAILED);
		}
	}

	public static class PayloadTooLarge extends HttpException {
		public PayloadTooLarge() {
			super(PAYLOAD_TOO_LARGE);
		}

		public PayloadTooLarge(String message) {
			super(message, PAYLOAD_TOO_LARGE);
		}

		public PayloadTooLarge(String message, Throwable cause) {
			super(message, cause, PAYLOAD_TOO_LARGE);
		}

		public PayloadTooLarge(Throwable cause) {
			super(cause, PAYLOAD_TOO_LARGE);
		}
	}

	@Deprecated
	public static class RequestEntityTooLarge extends HttpException {
		public RequestEntityTooLarge() {
			super(REQUEST_ENTITY_TOO_LARGE);
		}

		public RequestEntityTooLarge(String message) {
			super(message, REQUEST_ENTITY_TOO_LARGE);
		}

		public RequestEntityTooLarge(String message, Throwable cause) {
			super(message, cause, REQUEST_ENTITY_TOO_LARGE);
		}

		public RequestEntityTooLarge(Throwable cause) {
			super(cause, REQUEST_ENTITY_TOO_LARGE);
		}
	}

	public static class UriTooLong extends HttpException {
		public UriTooLong() {
			super(URI_TOO_LONG);
		}

		public UriTooLong(String message) {
			super(message, URI_TOO_LONG);
		}

		public UriTooLong(String message, Throwable cause) {
			super(message, cause, URI_TOO_LONG);
		}

		public UriTooLong(Throwable cause) {
			super(cause, URI_TOO_LONG);
		}
	}

	@Deprecated
	public static class RequestUriTooLong extends HttpException {
		public RequestUriTooLong() {
			super(REQUEST_URI_TOO_LONG);
		}

		public RequestUriTooLong(String message) {
			super(message, REQUEST_URI_TOO_LONG);
		}

		public RequestUriTooLong(String message, Throwable cause) {
			super(message, cause, REQUEST_URI_TOO_LONG);
		}

		public RequestUriTooLong(Throwable cause) {
			super(cause, REQUEST_URI_TOO_LONG);
		}
	}

	public static class UnsupportedMediaType extends HttpException {
		public UnsupportedMediaType() {
			super(UNSUPPORTED_MEDIA_TYPE);
		}

		public UnsupportedMediaType(String message) {
			super(message, UNSUPPORTED_MEDIA_TYPE);
		}

		public UnsupportedMediaType(String message, Throwable cause) {
			super(message, cause, UNSUPPORTED_MEDIA_TYPE);
		}

		public UnsupportedMediaType(Throwable cause) {
			super(cause, UNSUPPORTED_MEDIA_TYPE);
		}
	}

	public static class RequestedRangeNotSatisfiable extends HttpException {
		public RequestedRangeNotSatisfiable() {
			super(REQUESTED_RANGE_NOT_SATISFIABLE);
		}

		public RequestedRangeNotSatisfiable(String message) {
			super(message, REQUESTED_RANGE_NOT_SATISFIABLE);
		}

		public RequestedRangeNotSatisfiable(String message, Throwable cause) {
			super(message, cause, REQUESTED_RANGE_NOT_SATISFIABLE);
		}

		public RequestedRangeNotSatisfiable(Throwable cause) {
			super(cause, REQUESTED_RANGE_NOT_SATISFIABLE);
		}
	}

	public static class ExpectationFailed extends HttpException {
		public ExpectationFailed() {
			super(EXPECTATION_FAILED);
		}

		public ExpectationFailed(String message) {
			super(message, EXPECTATION_FAILED);
		}

		public ExpectationFailed(String message, Throwable cause) {
			super(message, cause, EXPECTATION_FAILED);
		}

		public ExpectationFailed(Throwable cause) {
			super(cause, EXPECTATION_FAILED);
		}
	}

	public static class IAmATeapot extends HttpException {
		public IAmATeapot() {
			super(I_AM_A_TEAPOT);
		}

		public IAmATeapot(String message) {
			super(message, I_AM_A_TEAPOT);
		}

		public IAmATeapot(String message, Throwable cause) {
			super(message, cause, I_AM_A_TEAPOT);
		}

		public IAmATeapot(Throwable cause) {
			super(cause, I_AM_A_TEAPOT);
		}
	}

	@Deprecated
	public static class InsufficientSpaceOnResource extends HttpException {
		public InsufficientSpaceOnResource() {
			super(INSUFFICIENT_SPACE_ON_RESOURCE);
		}

		public InsufficientSpaceOnResource(String message) {
			super(message, INSUFFICIENT_SPACE_ON_RESOURCE);
		}

		public InsufficientSpaceOnResource(String message, Throwable cause) {
			super(message, cause, INSUFFICIENT_SPACE_ON_RESOURCE);
		}

		public InsufficientSpaceOnResource(Throwable cause) {
			super(cause, INSUFFICIENT_SPACE_ON_RESOURCE);
		}
	}

	@Deprecated
	public static class MethodFailure extends HttpException {
		public MethodFailure() {
			super(METHOD_FAILURE);
		}

		public MethodFailure(String message) {
			super(message, METHOD_FAILURE);
		}

		public MethodFailure(String message, Throwable cause) {
			super(message, cause, METHOD_FAILURE);
		}

		public MethodFailure(Throwable cause) {
			super(cause, METHOD_FAILURE);
		}
	}

	@Deprecated
	public static class DestinationLocked extends HttpException {
		public DestinationLocked() {
			super(DESTINATION_LOCKED);
		}

		public DestinationLocked(String message) {
			super(message, DESTINATION_LOCKED);
		}

		public DestinationLocked(String message, Throwable cause) {
			super(message, cause, DESTINATION_LOCKED);
		}

		public DestinationLocked(Throwable cause) {
			super(cause, DESTINATION_LOCKED);
		}
	}

	public static class UnprocessableEntity extends HttpException {
		public UnprocessableEntity() {
			super(UNPROCESSABLE_ENTITY);
		}

		public UnprocessableEntity(String message) {
			super(message, UNPROCESSABLE_ENTITY);
		}

		public UnprocessableEntity(String message, Throwable cause) {
			super(message, cause, UNPROCESSABLE_ENTITY);
		}

		public UnprocessableEntity(Throwable cause) {
			super(cause, UNPROCESSABLE_ENTITY);
		}
	}

	public static class Locked extends HttpException {
		public Locked() {
			super(LOCKED);
		}

		public Locked(String message) {
			super(message, LOCKED);
		}

		public Locked(String message, Throwable cause) {
			super(message, cause, LOCKED);
		}

		public Locked(Throwable cause) {
			super(cause, LOCKED);
		}
	}

	public static class FailedDependency extends HttpException {
		public FailedDependency() {
			super(FAILED_DEPENDENCY);
		}

		public FailedDependency(String message) {
			super(message, FAILED_DEPENDENCY);
		}

		public FailedDependency(String message, Throwable cause) {
			super(message, cause, FAILED_DEPENDENCY);
		}

		public FailedDependency(Throwable cause) {
			super(cause, FAILED_DEPENDENCY);
		}
	}

	public static class TooEarly extends HttpException {
		public TooEarly() {
			super(TOO_EARLY);
		}

		public TooEarly(String message) {
			super(message, TOO_EARLY);
		}

		public TooEarly(String message, Throwable cause) {
			super(message, cause, TOO_EARLY);
		}

		public TooEarly(Throwable cause) {
			super(cause, TOO_EARLY);
		}
	}

	public static class UpgradeRequired extends HttpException {
		public UpgradeRequired() {
			super(UPGRADE_REQUIRED);
		}

		public UpgradeRequired(String message) {
			super(message, UPGRADE_REQUIRED);
		}

		public UpgradeRequired(String message, Throwable cause) {
			super(message, cause, UPGRADE_REQUIRED);
		}

		public UpgradeRequired(Throwable cause) {
			super(cause, UPGRADE_REQUIRED);
		}
	}

	public static class PreconditionRequired extends HttpException {
		public PreconditionRequired() {
			super(PRECONDITION_REQUIRED);
		}

		public PreconditionRequired(String message) {
			super(message, PRECONDITION_REQUIRED);
		}

		public PreconditionRequired(String message, Throwable cause) {
			super(message, cause, PRECONDITION_REQUIRED);
		}

		public PreconditionRequired(Throwable cause) {
			super(cause, PRECONDITION_REQUIRED);
		}
	}

	public static class TooManyRequests extends HttpException {
		public TooManyRequests() {
			super(TOO_MANY_REQUESTS);
		}

		public TooManyRequests(String message) {
			super(message, TOO_MANY_REQUESTS);
		}

		public TooManyRequests(String message, Throwable cause) {
			super(message, cause, TOO_MANY_REQUESTS);
		}

		public TooManyRequests(Throwable cause) {
			super(cause, TOO_MANY_REQUESTS);
		}
	}

	@Deprecated
	public static class RequestHeaderFieldsTooLarge extends HttpException {
		public RequestHeaderFieldsTooLarge() {
			super(REQUEST_ENTITY_TOO_LARGE);
		}

		public RequestHeaderFieldsTooLarge(String message) {
			super(message, REQUEST_ENTITY_TOO_LARGE);
		}

		public RequestHeaderFieldsTooLarge(String message, Throwable cause) {
			super(message, cause, REQUEST_ENTITY_TOO_LARGE);
		}

		public RequestHeaderFieldsTooLarge(Throwable cause) {
			super(cause, REQUEST_ENTITY_TOO_LARGE);
		}
	}

	public static class UnavailableForLegalReasons extends HttpException {
		public UnavailableForLegalReasons() {
			super(UNAVAILABLE_FOR_LEGAL_REASONS);
		}

		public UnavailableForLegalReasons(String message) {
			super(message, UNAVAILABLE_FOR_LEGAL_REASONS);
		}

		public UnavailableForLegalReasons(String message, Throwable cause) {
			super(message, cause, UNAVAILABLE_FOR_LEGAL_REASONS);
		}

		public UnavailableForLegalReasons(Throwable cause) {
			super(cause, UNAVAILABLE_FOR_LEGAL_REASONS);
		}
	}

	public static class InternalServerError extends HttpException {
		public InternalServerError() {
			super(INTERNAL_SERVER_ERROR);
		}

		public InternalServerError(String message) {
			super(message, INTERNAL_SERVER_ERROR);
		}

		public InternalServerError(String message, Throwable cause) {
			super(message, cause, INTERNAL_SERVER_ERROR);
		}

		public InternalServerError(Throwable cause) {
			super(cause, INTERNAL_SERVER_ERROR);
		}
	}

	public static class NotImplemented extends HttpException {
		public NotImplemented() {
			super(NOT_IMPLEMENTED);
		}

		public NotImplemented(String message) {
			super(message, NOT_IMPLEMENTED);
		}

		public NotImplemented(String message, Throwable cause) {
			super(message, cause, NOT_IMPLEMENTED);
		}

		public NotImplemented(Throwable cause) {
			super(cause, NOT_IMPLEMENTED);
		}
	}

	public static class BadGateway extends HttpException {
		public BadGateway() {
			super(BAD_GATEWAY);
		}

		public BadGateway(String message) {
			super(message, BAD_GATEWAY);
		}

		public BadGateway(String message, Throwable cause) {
			super(message, cause, BAD_GATEWAY);
		}

		public BadGateway(Throwable cause) {
			super(cause, BAD_GATEWAY);
		}
	}

	public static class ServiceUnavailable extends HttpException {
		public ServiceUnavailable() {
			super(SERVICE_UNAVAILABLE);
		}

		public ServiceUnavailable(String message) {
			super(message, SERVICE_UNAVAILABLE);
		}

		public ServiceUnavailable(String message, Throwable cause) {
			super(message, cause, SERVICE_UNAVAILABLE);
		}

		public ServiceUnavailable(Throwable cause) {
			super(cause, SERVICE_UNAVAILABLE);
		}
	}

	public static class GatewayTimeout extends HttpException {
		public GatewayTimeout() {
			super(GATEWAY_TIMEOUT);
		}

		public GatewayTimeout(String message) {
			super(message, GATEWAY_TIMEOUT);
		}

		public GatewayTimeout(String message, Throwable cause) {
			super(message, cause, GATEWAY_TIMEOUT);
		}

		public GatewayTimeout(Throwable cause) {
			super(cause, GATEWAY_TIMEOUT);
		}
	}

	public static class HttpVersionNotSupported extends HttpException {
		public HttpVersionNotSupported() {
			super(HTTP_VERSION_NOT_SUPPORTED);
		}

		public HttpVersionNotSupported(String message) {
			super(message, HTTP_VERSION_NOT_SUPPORTED);
		}

		public HttpVersionNotSupported(String message, Throwable cause) {
			super(message, cause, HTTP_VERSION_NOT_SUPPORTED);
		}

		public HttpVersionNotSupported(Throwable cause) {
			super(cause, HTTP_VERSION_NOT_SUPPORTED);
		}
	}

	public static class VariantAlsoNegotiates extends HttpException {
		public VariantAlsoNegotiates() {
			super(VARIANT_ALSO_NEGOTIATES);
		}

		public VariantAlsoNegotiates(String message) {
			super(message, VARIANT_ALSO_NEGOTIATES);
		}

		public VariantAlsoNegotiates(String message, Throwable cause) {
			super(message, cause, VARIANT_ALSO_NEGOTIATES);
		}

		public VariantAlsoNegotiates(Throwable cause) {
			super(cause, VARIANT_ALSO_NEGOTIATES);
		}
	}

	public static class InsufficientStorage extends HttpException {
		public InsufficientStorage() {
			super(INSUFFICIENT_STORAGE);
		}

		public InsufficientStorage(String message) {
			super(message, INSUFFICIENT_STORAGE);
		}

		public InsufficientStorage(String message, Throwable cause) {
			super(message, cause, INSUFFICIENT_STORAGE);
		}

		public InsufficientStorage(Throwable cause) {
			super(cause, INSUFFICIENT_STORAGE);
		}
	}

	public static class LoopDetected extends HttpException {
		public LoopDetected() {
			super(LOOP_DETECTED);
		}

		public LoopDetected(String message) {
			super(message, LOOP_DETECTED);
		}

		public LoopDetected(String message, Throwable cause) {
			super(message, cause, LOOP_DETECTED);
		}

		public LoopDetected(Throwable cause) {
			super(cause, LOOP_DETECTED);
		}
	}

	public static class NotExtended extends HttpException {
		public NotExtended() {
			super(NOT_EXTENDED);
		}

		public NotExtended(String message) {
			super(message, NOT_EXTENDED);
		}

		public NotExtended(String message, Throwable cause) {
			super(message, cause, NOT_EXTENDED);
		}

		public NotExtended(Throwable cause) {
			super(cause, NOT_EXTENDED);
		}
	}

	public static class NetworkAuthenticationRequired extends HttpException {
		public NetworkAuthenticationRequired() {
			super(NETWORK_AUTHENTICATION_REQUIRED);
		}

		public NetworkAuthenticationRequired(String message) {
			super(message, NETWORK_AUTHENTICATION_REQUIRED);
		}

		public NetworkAuthenticationRequired(String message, Throwable cause) {
			super(message, cause, NETWORK_AUTHENTICATION_REQUIRED);
		}

		public NetworkAuthenticationRequired(Throwable cause) {
			super(cause, NETWORK_AUTHENTICATION_REQUIRED);
		}
	}
}
