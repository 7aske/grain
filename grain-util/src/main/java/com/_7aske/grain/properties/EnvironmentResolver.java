package com._7aske.grain.properties;

import com._7aske.grain.annotation.NotNull;

import java.util.Locale;

public class EnvironmentResolver {
	public interface PropertiesEnvironmentConsumer {
		void accept(String key, String value);
	}

	public void resolve(PropertiesEnvironmentConsumer consumer) {
		System.getenv().forEach((key, value) -> {
			String adapted = adaptEnvKey(key);
			if (!adapted.isEmpty() && Character.isLetter(key.charAt(0))) {
				consumer.accept(adapted, value);
			}
		});
	}

	private @NotNull String adaptEnvKey(@NotNull String key) {
		return key.replace("_", ".")
				.toLowerCase(Locale.ROOT);
	}
}
