package com._7aske.grain.util;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
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
	private final Constructor<MethodHandles.Lookup> constructor;
	private static final Logger logger = LoggerFactory.getLogger(ProxyInvocationHandler.class);


	public ProxyInvocationHandler() throws NoSuchMethodException {
		this.constructor = MethodHandles.Lookup.class
				.getDeclaredConstructor(Class.class);
		this.constructor.setAccessible(true);
	}

	public synchronized MethodHandle getInstance(Method method) throws Exception {
		if (cache.containsKey(method)) {
			return cache.get(method);
		}
		MethodHandle instance = this.constructor.newInstance(method.getDeclaringClass())
				.in(method.getDeclaringClass())
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
		logger.warn("Proxy call on a non-default method '{}'", method);
		return null;
	}
}
