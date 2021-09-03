package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ControllerMethodWrapper {
	private final Method method;
	private final String path;
	private final HttpMethod httpMethod;

	public ControllerMethodWrapper(Method method) {
		this.method = method;
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		this.httpMethod = requestMapping.method();
		this.path = requestMapping.value().startsWith("/") ? requestMapping.value() : "/" + requestMapping.value();
	}

	public Object invoke(Object instance, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new HttpException.InternalServerError(e);
		}
	}

	public int getParameterCount() {
		return method.getParameterCount();
	}

	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getPath() {
		return path;
	}
}
