package com._7aske.grain;

import com._7aske.grain.exception.AppInitializationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GrainAppRunner {
	private GrainAppRunner(){}

	public static <T extends GrainApp> GrainApp run(Class<T> clazz){
		Constructor<T> constructor = getAnyConstructor(clazz);
		try {
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new AppInitializationException("Failed to initialize Grain App", e);
		}
	}

	public static <T extends GrainApp> GrainApp run(Class<T> clazz, String[] args){
		Constructor<T> constructor = getAnyConstructor(clazz);
		try {
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new AppInitializationException("Failed to initialize Grain App", e);
		}
	}


	private static  <T extends GrainApp> Constructor<T> getAnyConstructor(Class<T> clazz) {
		Constructor<T> constructor = null;
		Throwable cause = null;
		try {
			constructor = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			cause = e;
		}

		if (constructor == null) {
			try {
				constructor = clazz.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
				cause = e;
			}
		}

		if (constructor == null) {
			constructor = (Constructor<T>) clazz.getEnclosingConstructor();
		}

		if (constructor != null)
			return constructor;
		throw new AppInitializationException("Failed to initialize Grain App", cause);
	}
}
