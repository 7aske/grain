package com._7aske.grain.web.requesthandler.controller.wrapper;

import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.component.Ordered;
import com._7aske.grain.web.controller.exceptionhandler.ExceptionHandler;
import com._7aske.grain.web.http.HttpRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Wrapper around a controller Grain component method responsible for handling {@link HttpRequest}s.
 */
public class ExceptionControllerMethodWrapper extends AbstractControllerMethodWrapper implements Ordered {
	private final ExceptionHandler exceptionHandler;

	public ExceptionControllerMethodWrapper(Method method, Object controllerInstance) {
        super(method, controllerInstance);
        this.exceptionHandler = Objects.requireNonNull(method.getAnnotation(ExceptionHandler.class));
    }

	@Override
	public Object invoke(Object... args) throws Exception {
		Parameter[] parameters = getParameters();
		Object[] actualArgs = new Object[parameters.length];

        for (Object arg : args) {
            for (int j = 0; j < parameters.length; j++) {
                Parameter parameter = parameters[j];
                if (parameter.getType().isAssignableFrom(arg.getClass())) {
                    actualArgs[j] = arg;
                    break;
                }
            }
        }

		return super.invoke(actualArgs);
	}

	public boolean isVoidReturnType() {
		return method.getReturnType().equals(Void.TYPE);
	}

	@Override
	public int getOrder() {
		return Optional.ofNullable(method.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Order.DEFAULT);
	}

	public List<Class<? extends Throwable>> getExceptions() {
		return Arrays.asList(exceptionHandler.value());
	}

	public boolean canHandle(Throwable exception) {
		return getExceptions().stream().anyMatch(e -> e.isAssignableFrom(exception.getClass()));
	}
}
