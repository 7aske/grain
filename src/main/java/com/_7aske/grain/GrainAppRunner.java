package com._7aske.grain;

import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GrainAppRunner {
	private GrainAppRunner(){}

	public static <T extends GrainApp> GrainApp run(Class<T> clazz){
		Constructor<T> constructor = getAnyConstructor(clazz);
		try {
			T app = constructor.newInstance();
			app.setBasePackage(clazz.getPackageName());
			app.run();
			return app;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new AppInitializationException("Failed to initialize Grain App", e);
		}
	}


	private static  <T extends GrainApp> Constructor<T> getAnyConstructor(Class<T> clazz) {
		try {
			return ReflectionUtil.getAnyConstructor(clazz);
		} catch (NoSuchMethodException ex) {
			throw new AppInitializationException("Failed to initialize Grain App", ex);
		}
	}
}
