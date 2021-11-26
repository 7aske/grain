package com._7aske.grain.util.classloader;

import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrainJarClassLoader implements GrainClassLoader {
	private final String basePackage;
	private final ClassLoader classLoader;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Set<URL> jars;

	public GrainJarClassLoader(String basePackage) {
		this.basePackage = basePackage;
		this.classLoader = ClassLoader.getSystemClassLoader();
		this.jars = new HashSet<>();
	}

	private Set<String> doLoadClasses(String path) throws IOException {

		Set<URL> urls = getUrlsFromResource(path.replaceAll("\\.", "/"));

		Set<String> paths = new HashSet<>();
		for (URL url : urls) {
			InputStream stream = url.openStream();

			if (stream == null) {
				continue;
			}

			if (url.getProtocol().equals("jar")) {
				String jarFilePath = url.toExternalForm().split("!")[0];
				jars.add(new URL(jarFilePath + "!/"));
				paths.addAll(getClassNamesFromJarFile(new File(jarFilePath.replace("jar:file:", ""))));
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				paths.addAll(reader.lines().map(line -> path + "." + line).collect(Collectors.toList()));
			}

		}

		return paths.stream()
				.flatMap(p -> {
					if (p.endsWith(".class")) {
						return Stream.of(p);
					} else {
						try {
							return doLoadClasses(p).stream();
						} catch (IOException e) {
							return Stream.empty();
						}
					}
				})
				.collect(Collectors.toSet());
	}

	private Set<URL> getUrlsFromResource(String resourceName) {
		Set<URL> result = new HashSet<>();
		try {
			final Enumeration<URL> urls = classLoader.getResources(resourceName);
			while (urls.hasMoreElements()) {
				final URL url = urls.nextElement();
				result.add(url);
			}
		} catch (IOException e) {
		}
		return result;
	}

	@Override
	public Set<Class<?>> loadClasses(Predicate<Class<?>> predicate) {
		final URLClassLoader urlClassLoader = new URLClassLoader(jars.toArray(new URL[0]));
		try {
			return doLoadClasses(basePackage)
					.stream()
					.map(c -> {
						try {
							String className = c.endsWith(".class") ? c.substring(0, c.length() - ".class".length()) : c;
							return Class.forName(className, true, urlClassLoader);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							return null;
						}
					})
					.filter(Objects::nonNull)
					.filter(predicate)
					.peek(c -> {
						logger.debug("Loading {}", c);
					})
					.collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
			return new HashSet<>();
		}
	}

	@Override
	public Set<Class<?>> loadClasses() {
		return loadClasses(c -> true);
	}

	public static Set<String> getClassNamesFromJarFile(File givenFile) throws IOException {
		Set<String> classNames = new HashSet<>();
		try (JarFile jarFile = new JarFile(givenFile)) {
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry jarEntry = e.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					String className = jarEntry.getName()
							.replace("/", ".");
					classNames.add(className);
				}
			}
			return classNames;
		}
	}
}
