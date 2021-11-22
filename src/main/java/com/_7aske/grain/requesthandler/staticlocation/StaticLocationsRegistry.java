package com._7aske.grain.requesthandler.staticlocation;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StaticLocationsRegistry {
	public static final String RESOURCES_PREFIX = "resources:";
	private final List<String> locations = new ArrayList<>();

	private StaticLocationsRegistry() {
		locations.add("resources:/static");
		locations.add("resources:/public");
	}

	public static StaticLocationsRegistry createDefault() {
		return new StaticLocationsRegistry();
	}

	public void addStaticLocation(String path) {
		if (path.startsWith(RESOURCES_PREFIX)) {
			locations.add(path);
		} else {
			locations.add(Paths.get(path).toAbsolutePath().toString());
		}
	}

	public void removeStaticLocation(String path) {
		if (path.startsWith(RESOURCES_PREFIX)) {
			locations.remove(path);
		} else {
			locations.remove(Paths.get(path).toAbsolutePath().toString());
		}
	}

	public List<String> getStaticLocations() {
		return this.locations;
	}
}
