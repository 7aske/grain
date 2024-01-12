package com._7aske.grain.core.reflect;

import com._7aske.grain.exception.GrainReflectionException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Invocation handler that is used to create and cache MethodHandles for all method
 * invocations for a given proxy instance.
 */
public class ProxyInvocationHandler implements InvocationHandler {
	private final Map<Method, MethodHandle> cache = new ConcurrentHashMap<>();

	public synchronized MethodHandle getInstance(Method method) throws Exception {
		if (cache.containsKey(method)) {
			return cache.get(method);
		}
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle instance = MethodHandles.privateLookupIn(method.getDeclaringClass(), lookup)
				.unreflectSpecial(method, method.getDeclaringClass());
		cache.put(method, instance);
		return instance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.isDefault()) {
			return getInstance(method)
					.bindTo(proxy)
					.invokeWithArguments(args);
		}

		throw new GrainReflectionException("Proxy call on a non-default method");
	}
}
