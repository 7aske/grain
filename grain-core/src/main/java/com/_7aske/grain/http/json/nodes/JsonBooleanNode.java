package com._7aske.grain.http.json.nodes;

public class JsonBooleanNode extends JsonNode {

    public JsonBooleanNode(Boolean value) {
        super(value);
    }

    /**
     * @param key 
     * @return
     */
    @Override
    public JsonNode get(String key) {
        throw new UnsupportedOperationException("Cannot call get(String) on JsonBooleanNode");
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
        throw new UnsupportedOperationException("Cannot call getString() on JsonBooleanNode");
    }

    /**
     * @return 
     */
    @Override
    public Number getNumber() {
        throw new UnsupportedOperationException("Cannot call getNumber() on JsonBooleanNode");
    }

    /**
     * @return 
     */
    @Override
    public Boolean getBoolean() {
        return (Boolean) value;
    }

    /**
     * @return
     */
    @Override
    public JsonObjectNode getObject() {
        throw new UnsupportedOperationException("Cannot call getObject() on JsonBooleanNode");
    }

    /**
     * @return 
     */
    @Override
    public JsonArrayNode getArray() {
        throw new UnsupportedOperationException("Cannot call getArray() on JsonBooleanNode");
    }

    /**
     * @param key 
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (!Boolean.class.isAssignableFrom(clazz)) {
            throw new UnsupportedOperationException("Cannot call get(String, Class<" + clazz.getName() + ">) on JsonBooleanNode");
        }

        return clazz.cast(value);
    }
}
