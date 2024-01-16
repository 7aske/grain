package com._7aske.grain.web.http.codec.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class JsonObject implements JsonString {
	private final Map<String, Object> data;

	public JsonObject() {
		this.data = new HashMap<>();
	}

	public static <T> JsonObject of(T object) {
		JsonSerializer<T> deserializer = new JsonSerializer<T>((Class<T>) object.getClass());
		return (JsonObject) deserializer.serialize(object);
	}

	public JsonObject(Map<String, Object> data) {
		this();
		for (Map.Entry<String, Object> kv : data.entrySet()) {
			String key = kv.getKey();
			Object value = kv.getValue();
			if (value instanceof Map) {
				this.data.put(key, new JsonObject((Map<String, Object>) value));
			} else if (value instanceof List) {
				this.data.put(key, new JsonArray((List<Object>) value));
			} else {
				this.data.put(key, value);
			}
		}
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(data.get(key));
	}

	public Object get(String key) {
		return data.get(key);
	}

	public JsonObject getObject(String key) {
		return (JsonObject) data.get(key);
	}

	public JsonObject putObject(String key, JsonObject object) {
		return (JsonObject) data.put(key, object);
	}

	public Object putNull(String key) {
		return (Object) data.put(key, null);
	}

	public String getString(String key) {
		return (String) data.get(key);
	}

	public String putString(String key, String string) {
		return (String) data.put(key, string);
	}

	public JsonArray getArray(String key) {
		return (JsonArray) data.get(key);
	}

	public JsonArray putArray(String key, JsonArray array) {
		return (JsonArray) data.put(key, array);
	}

	public JsonArray putArray(String key, List<Object> list) {
		return (JsonArray) data.put(key, new JsonArray(list));
	}

	public Number getNumber(String key) {
		return (Number) data.get(key);
	}

	public Number putNumber(String key, Number number) {
		return (Number) data.put(key, number);
	}

	public Boolean getBoolean(String key) {
		return (Boolean) data.get(key);
	}

	public Boolean putBoolean(String key, Boolean bool) {
		return (Boolean) data.put(key, bool);
	}

	public String toJsonString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");

		for (Map.Entry<String, Object> kv : this.data.entrySet()) {
			Object value = kv.getValue();
			builder.append(String.format("\"%s\"", kv.getKey()));
			builder.append(":");
			if (value == null) {
				builder.append("null");
				builder.append(",");
				continue;
			}

			if (value instanceof JsonObject) {
				builder.append(((JsonObject) value).toJsonString());
			} else if (value instanceof JsonArray) {
				builder.append(((JsonArray) value).toJsonString());
			} else if (value instanceof Boolean) {
				builder.append(((Boolean) value).booleanValue());
			} else if (value instanceof String) {
				builder.append(String.format("\"%s\"", value));
			} else if (value instanceof Number) {
				builder.append(value);
			}

			builder.append(",");
		}

		if (builder.charAt(builder.length() - 1) == ',') {
			builder.setLength(builder.length() - 1);
		}

		builder.append("}");
		return builder.toString();
	}


}

