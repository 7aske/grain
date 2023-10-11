package com._7aske.grain.http.json.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonObjectNode extends JsonNode implements Map<String, JsonNode> {

	public JsonObjectNode() {
		super(new HashMap<>());
	}

	@Override
	public JsonObjectNode asObject() {
		return this;
	}

	@Override
	public int size() {
		return getValueAsMap().size();
	}

	@Override
	public boolean isEmpty() {
		return getValueAsMap().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return getValueAsMap().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getValueAsMap().containsValue(value);
	}

	@Override
	public JsonNode get(Object key) {
		return super.get((String) key);
	}

	@Override
	public JsonNode put(String key, JsonNode value) {
		return getValueAsMap().put(key, value);
	}

	public JsonNode putString(String key, String value) {
		if (value == null) {
			return putNull(key);
		}
		return getValueAsMap().put(key, new JsonStringNode(value));
	}

	public JsonNode putNumber(String key, Number value) {
		if (value == null) {
			return putNull(key);
		}
		return getValueAsMap().put(key, new JsonNumberNode(value));
	}

	public JsonNode putBoolean(String key, Boolean value) {
		if (value == null) {
			return putNull(key);
		}
		return getValueAsMap().put(key, new JsonBooleanNode(value));
	}

	public JsonNode putObject(String key, JsonObjectNode value) {
		if (value == null) {
			return putNull(key);
		}
		return getValueAsMap().put(key, value);
	}

	public JsonNode putArray(String key, JsonArrayNode value) {
		if (value == null) {
			return putNull(key);
		}
		return getValueAsMap().put(key, value);
	}

	public JsonNode putNull(String key) {
		return getValueAsMap().put(key, new JsonNullNode());
	}

	@Override
	public JsonNode remove(Object key) {
		return getValueAsMap().remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends JsonNode> m) {
		getValueAsMap().putAll(m);
	}

	@Override
	public void clear() {
		getValueAsMap().clear();
	}

	@Override
	public Set<String> keySet() {
		return getValueAsMap().keySet();
	}

	/**
	 * @return 
	 */
	@Override
	public Collection<JsonNode> values() {
		return getValueAsMap().values();
	}

	/**
	 * @return 
	 */
	@Override
	public Set<Entry<String, JsonNode>> entrySet() {
		return getValueAsMap().entrySet();
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

