package com._7aske.grain.http.json.nodes;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectNode extends JsonNode {

	public JsonObjectNode() {
		super(new HashMap<String, JsonNode>());
	}

	@Override
	public JsonNode get(String key) {
		return ((Map<String, JsonNode>) value).get(key);
	}

	@Override
	public Object getValue() {
		return value;
	}

	public String getString() {
		throw new UnsupportedOperationException("Cannot call getString() on JsonObjectNode");
	}

	@Override
	public Number getNumber() {
		throw new UnsupportedOperationException("Cannot call getString() on JsonObjectNode");
	}

	@Override
	public Boolean getBoolean() {
		throw new UnsupportedOperationException("Cannot call getString() on JsonObjectNode");
	}

	@Override
	public JsonObjectNode getObject() {
		return this;
	}

	@Override
	public JsonArrayNode getArray() {
		throw new UnsupportedOperationException("Cannot call getString() on JsonObjectNode");
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(get(key));
	}

	public void put(String key, JsonNode value) {
		((Map<String, JsonNode>) this.value).put(key, value);
	}

//	public static <T> JsonObject of(T object) {
//		JsonSerializer<T> deserializer = new JsonSerializer<T>((Class<T>) object.getClass());
//		return (JsonObject) deserializer.serialize(object);
//	}

//	public JsonObject(Map<String, Object> data) {
//		this();
//		for (Map.Entry<String, Object> kv : data.entrySet()) {
//			String key = kv.getKey();
//			Object value = kv.getValue();
//			if (value instanceof Map) {
//				this.data.put(key, new JsonObject((Map<String, Object>) value));
//			} else if (value instanceof List) {
//				this.data.put(key, new JsonArray((List<Object>) value));
//			} else {
//				this.data.put(key, value);
//			}
//		}
//	}
}

