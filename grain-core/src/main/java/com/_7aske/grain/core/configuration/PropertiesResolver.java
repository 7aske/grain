package com._7aske.grain.core.configuration;

import java.io.*;
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

	public void resolve(String[] filePaths, PropertiesInputStreamConsumer inputStreamConsumer) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (String filePath : filePaths) {
			resolve(filePath, is -> {
				is.transferTo(byteArrayOutputStream);
				byteArrayOutputStream.write('\n');
			});
		}
		ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		try {
			inputStreamConsumer.accept(byteArrayInputStream);
		} catch (IOException e) {/*ignored*/}
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
