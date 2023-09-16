package com._7aske.grain.http.json.nodes;

import java.util.Map;

public abstract class JsonNode<T> {
    // String, Number, Boolean, Map<String, JsonNode>, List<JsonNode>
    protected T value;

    protected JsonNode() {
    }

    protected JsonNode(T value) {
        this.value = value;
    }

    // @Todo replace with List and Map specific get
    public JsonNode<?> get(String key) {
        if (value instanceof Map) {
            return ((Map<String, JsonNode>) value).get(key);
        }

        throw new UnsupportedOperationException("Cannot get key from non-object value");
    }

    /**
     * @return Raw value stored in the json object.
     */
    public Object getValue() {
        return value;
    }

    public String getString() {
        return (String) value;
    }

    public Number getNumber() {
        return (Number) value;
    }

    public Boolean getBoolean() {
        return (Boolean) value;
    }

    public JsonObjectNode asObject() {
        throw new UnsupportedOperationException("asObject() is not supported for this type");
    }

    public JsonArrayNode asArray() {
        throw new UnsupportedOperationException("asArray() is not supported for this type");
    }
}
