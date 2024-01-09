package com._7aske.grain.web.requesthandler.controller;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.util.HttpPathUtil;
import com._7aske.grain.web.controller.exception.NoValidConverterException;
import com._7aske.grain.web.controller.parameter.ParameterConverterRegistry;
import com._7aske.grain.web.controller.response.ResponseWriterRegistry;
import com._7aske.grain.web.http.GrainHttpResponse;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerMethodWrapper;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This {@link RequestHandler} implementation is a representation of a controller method.
 */
public class ControllerMethodHandler implements RequestHandler {
	private final ControllerMethodWrapper method;
	private final ParameterConverterRegistry parameterConverterRegistry;
	private final ResponseWriterRegistry responseWriterRegistry;

	public ControllerMethodHandler(ControllerMethodWrapper method,
                                   ParameterConverterRegistry parameterConverterRegistry,
								   ResponseWriterRegistry responseWriterRegistry) {
		this.method = method;
        this.parameterConverterRegistry = parameterConverterRegistry;
        this.responseWriterRegistry = responseWriterRegistry;
    }

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws IOException {

		Object[] params = Arrays.stream(method.getParameters())
				.map(param -> parameterConverterRegistry.getConverter(param)
						.map(converter -> converter.convert(param, request, response, this))
						.orElseThrow(() -> new NoValidConverterException(param.getType())))
				.toArray(Object[]::new);

		final Object result = method.invoke(params);

		responseWriterRegistry.getWriter(result)
				.ifPresent(writer -> {
					try {
						writer.write(result, response, request, this);
					} catch (IOException e) {
						throw new GrainRuntimeException(e);
					}
				});


		// Finally, we need to set the request handled attribute to true
		// so that we don't get 404 exception from the HandlerRunner.
		// @Hack
		if (response instanceof GrainHttpResponse res) {
			res.setCommitted(true);
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
}
