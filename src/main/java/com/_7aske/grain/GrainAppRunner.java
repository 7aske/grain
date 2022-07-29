package com._7aske.grain;

import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Grain application runner responsible for handling initialization of the
 * application itself by passing the required package argument.
 */
public final class GrainAppRunner {
	private static final Logger logger = LoggerFactory.getLogger(GrainApp.class);

	private GrainAppRunner(){}

	public static void main(String[] args) {
		GrainAppRunner.run(GrainApp.class);
	}

	public static <T extends GrainApp> void run(Class<T> clazz){
		final long startTime = System.currentTimeMillis();
		Constructor<T> constructor = getAnyConstructor(clazz);
		try {
			T app = constructor.newInstance();
			app.initialize(clazz.getPackageName());
			logger.debug("Startup took {}ms", System.currentTimeMillis() - startTime);
			app.run();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new AppInitializationException("Failed to initialize Grain App", e);
		}
	}

	// Loads the default protected constructor of the GrainApp class
	private static <T extends GrainApp> Constructor<T> getAnyConstructor(Class<T> clazz) {
		try {
			return ReflectionUtil.getAnyConstructor(clazz);
		} catch (NoSuchMethodException ex) {
			throw new AppInitializationException("Failed to initialize Grain App", ex);
		}
	}
}
