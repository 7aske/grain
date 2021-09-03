package com._7aske.grain.http.json;

import java.util.*;

public class JsonArray implements Collection<Object> {
	private final List<Object> data;

	public JsonArray() {
		this.data = new ArrayList<>();
	}

	public JsonArray(List<Object> objectList) {
		this();
		for (Object value : objectList) {
			if (value instanceof Map) {
				this.data.add(new JsonObject((Map<String, Object>) value));
			} else if (value instanceof List) {
				this.data.add(new JsonArray((List<Object>) value));
			} else {
				this.data.add(value);
			}
		}
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return data.contains(o);
	}

	@Override
	public Iterator<Object> iterator() {
		return data.iterator();
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean add(Object object) {
		return data.add(object);
	}

	@Override
	public boolean remove(Object object) {
		return data.remove(object);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<?> c) {
		return data.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return data.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	@Override
	public void clear() {
		data.clear();
	}

	public String toJsonString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (Object value : this.data) {
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

		builder.append("]");
		return builder.toString();
	}
}

