package com._7aske.grain.web.requesthandler.controller.wrapper;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.ReflectionUtil;
import com._7aske.grain.web.controller.annotation.ResponseStatus;
import com._7aske.grain.web.http.HttpRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Wrapper around a controller Grain component method responsible for handling {@link HttpRequest}s.
 */
public abstract class AbstractControllerMethodWrapper {
	protected final Method method;
	protected final Object controllerInstance;
	protected final ResponseStatus responseStatus;
	protected final Logger logger;

	protected AbstractControllerMethodWrapper(Method method, Object controllerInstance) {
		this.method = method;
		this.controllerInstance = controllerInstance;
		this.responseStatus = getResponseStatus(method);
		this.logger = LoggerFactory.getLogger(method.getDeclaringClass());
	}

	protected ResponseStatus getResponseStatus(Method method) {
		ResponseStatus retVal = method.getAnnotatedReturnType().getAnnotation(ResponseStatus.class);
		if (retVal != null)
			return retVal;

		return method.getAnnotation(ResponseStatus.class);
	}

	public Object invoke(Object... args) throws Exception {
		try {
			logger.trace("Invoking method {}", method.getName());
			return ReflectionUtil.invokeMethod(method, controllerInstance, args);
		} catch (Exception e) {
			Exception cause = (Exception) e.getCause();
			if (cause == null) throw e;
			throw cause;
		}
	}

	public Parameter[] getParameters() {
		return method.getParameters();
	}

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}
}
