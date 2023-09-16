package com._7aske.grain.http.json.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonObjectNode extends JsonNode<Map<String, JsonNode<?>>> implements Map<String, JsonNode<?>> {

	public JsonObjectNode() {
		super(new HashMap<>());
	}

	@Override
	public JsonObjectNode asObject() {
		return this;
	}

	@Override
	public int size() {
		return this.value.size();
	}

	@Override
	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.value.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.value.containsValue(value);
	}

	@Override
	public JsonNode<?> get(Object key) {
		return super.get((String) key);
	}

	@Override
	public JsonNode<?> put(String key, JsonNode<?> value) {
		return this.value.put(key, value);
	}

	@Override
	public JsonNode<?> remove(Object key) {
		return this.value.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends JsonNode<?>> m) {
		this.value.putAll(m);
	}

	@Override
	public void clear() {
		this.value.clear();
	}

	@Override
	public Set<String> keySet() {
		return this.value.keySet();
	}

	/**
	 * @return 
	 */
	@Override
	public Collection<JsonNode<?>> values() {
		return this.value.values();
	}

	/**
	 * @return 
	 */
	@Override
	public Set<Entry<String, JsonNode<?>>> entrySet() {
		return this.value.entrySet();
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

