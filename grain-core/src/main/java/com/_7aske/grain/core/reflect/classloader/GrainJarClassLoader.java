package com._7aske.grain.core.reflect.classloader;

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
				paths.addAll(getClassNamesFromJarFile(new File(jarFilePath.replace("jar:file:", "")), path));
			} else {
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
					paths.addAll(reader.lines().map(line -> path + "." + line).collect(Collectors.toList()));
				}
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
		try (final URLClassLoader urlClassLoader = new URLClassLoader(jars.toArray(new URL[0]))) {
			return doLoadClasses(basePackage)
					.stream()
					.filter(url -> !url.startsWith("META-INF"))
					.map(c -> {
						try {
							String className = c.endsWith(".class") ? c.substring(0, c.length() - ".class".length()) : c;
							if (className.startsWith("."))
								className = className.substring(1);
							return Class.forName(className, false, urlClassLoader);
						} catch (ClassNotFoundException | NoClassDefFoundError e) {
							return null;
						}
					})
					.filter(Objects::nonNull)
					.filter(predicate)
					.map(c -> {
						logger.debug("Loading {}", c);
						return c;
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

	public static Set<String> getClassNamesFromJarFile(File givenFile, String basePackage) throws IOException {
		Set<String> classNames = new HashSet<>();
		try (JarFile jarFile = new JarFile(givenFile)) {
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry jarEntry = e.nextElement();
				if (jarEntry.getName().endsWith(".class")) {
					String className = jarEntry.getName()
							.replace("/", ".");
					// @CopyPasta ReflectionUtil
					// @Note this check allows only classes that are starting with
					// the base package of the framework and base package of the
					// user's application to be loaded from jars. This will prevent
					// scanning the whole classpath and speed application startup.
					if (className.startsWith(basePackage) &&
							className.charAt(basePackage.length()) == '.') {
						classNames.add(className);
					}
				}
			}
			return classNames;
		}
	}
}
