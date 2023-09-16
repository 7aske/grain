package com._7aske.grain.http.json.nodes;

public class JsonNumberNode extends JsonNode {
    public JsonNumberNode(Number value) {
        super(value);
    }

    /**
     * @param key 
     * @return
     */
    @Override
    public JsonNode get(String key) {
        throw new UnsupportedOperationException("Cannot call get(String) on JsonNumberNode");
    }

    /**
     * @return 
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * @return 
     */
    @Override
    public String getString() {
        throw new UnsupportedOperationException("Cannot call getString() on JsonNumberNode");
    }

    /**
     * @return 
     */
    @Override
    public Number getNumber() {
        return (Number) value;
    }

    /**
     * @return 
     */
    @Override
    public Boolean getBoolean() {
        throw new UnsupportedOperationException("Cannot call getBoolean() on JsonNumberNode");
    }

    /**
     * @return
     */
    @Override
    public JsonObjectNode getObject() {
        throw new UnsupportedOperationException("Cannot call getObject() on JsonNumberNode");
    }

    /**
     * @return 
     */
    @Override
    public JsonArrayNode getArray() {
        throw new UnsupportedOperationException("Cannot call getArray() on JsonNumberNode");
    }

    /**
     * @param key 
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (!Number.class.isAssignableFrom(clazz)) {
            throw new UnsupportedOperationException("Cannot call get(String, Class<" + clazz.getName() + ">) on JsonNumberNode");
        }

        return clazz.cast(value);
    }
}
