package com._7aske.grain.core.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class PropertiesResolver {
	public interface PropertiesInputStreamConsumer {
		void accept(InputStream inputStream) throws IOException;
	}

	private static final String PATH_SUFFIX = ".properties";

	private final ClassLoader classLoader;
	private final List<String> profiles;

	public PropertiesResolver(List<String> profiles) {
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.profiles = profiles;
	}

	public void resolve(String filePath, PropertiesInputStreamConsumer inputStreamConsumer) {
		Stream.concat(profiles.stream().map(profile -> filePath + "-" + profile), Stream.of(filePath))
				.flatMap(path -> {
					try {
						return Collections.list(classLoader.getResources(path + PATH_SUFFIX)).stream();
					} catch (IOException ignored) {
						return Stream.empty();
					}
				})
				.forEach(url -> {
					try (InputStream inputStream = url.openStream()) {
						inputStreamConsumer.accept(inputStream);
					} catch (IOException ignored) {/*ignored*/}
				});
	}
}
