package com._7aske.grain.web.requesthandler.controller;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.web.http.GrainRequestHandlerException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.web.util.HttpPathUtil;
import com._7aske.grain.web.controller.ResponseStatusResolver;
import com._7aske.grain.web.controller.exception.NoValidConverterException;
import com._7aske.grain.web.controller.parameter.ParameterConverterRegistry;
import com._7aske.grain.web.controller.response.ResponseWriter;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerMethodWrapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This {@link RequestHandler} implementation is a representation of a controller method.
 */
public class ControllerMethodHandler implements RequestHandler {
	private final ControllerMethodWrapper method;
	private final ParameterConverterRegistry parameterConverterRegistry;
	private final ResponseWriterRegistry responseWriterRegistry;
	private final Logger logger;

	public ControllerMethodHandler(ControllerMethodWrapper method,
                                   ParameterConverterRegistry parameterConverterRegistry,
								   ResponseWriterRegistry responseWriterRegistry) {
		this.method = method;
        this.parameterConverterRegistry = parameterConverterRegistry;
        this.responseWriterRegistry = responseWriterRegistry;
		this.logger = LoggerFactory.getLogger(method.getName());
    }

	@Override
	public void handle(HttpRequest request, HttpResponse response) {
		try {
			logger.debug("Handling {} {}", request.getMethod(), request.getPath());

			Object[] params = Arrays.stream(method.getParameters())
					.map(param -> parameterConverterRegistry.getConverter(param)
							.orElseThrow(() -> new NoValidConverterException(param.getType()))
							.convert(param, request, response, this))
					.toArray(Object[]::new);

			response.setStatus(ResponseStatusResolver.resolveStatus(method.getResponseStatus()));

			final Object result = method.invoke(params);

			Optional<ResponseWriter<?>> writer = responseWriterRegistry.getWriter(result);
			if (writer.isPresent()) {
				writer.get().write(result, request, response, this);
			}


			// Finally, we need to set the request handled attribute to true
			// so that we don't get 404 exception from the HandlerRunner.
			// @Hack
			if (response instanceof GrainHttpResponse res) {
				res.setCommitted(true);
			}
		} catch (Exception e) {
			logger.error("Error while handling request", e);
			throw new GrainRequestHandlerException(e);
		}
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		List<HttpMethod> httpMethods = method.getHttpMethods();
		boolean canHandleMethod = httpMethods.isEmpty() || httpMethods.contains(request.getMethod());
		boolean canHandlePath = HttpPathUtil.arePathsMatching(request.getPath(), method.getPath());

		return canHandleMethod && canHandlePath;
	}

	@Override
	public @NotNull String getPath() {
		return method.getPath();
	}

	@Override
	public Collection<HttpMethod> getMethods() {
		return method.getHttpMethods();
	}
}
