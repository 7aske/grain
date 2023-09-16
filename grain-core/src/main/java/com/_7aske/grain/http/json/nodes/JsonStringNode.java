package com._7aske.grain.http.json.nodes;

public class JsonStringNode extends JsonNode {
    public JsonStringNode(String value) {
        super(value);
    }

    /**
     * @param key 
     * @return
     */
    @Override
    public JsonNode get(String key) {
        throw new UnsupportedOperationException("Cannot call get(String) on JsonStringNode");
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
        return (String) value;
    }

    /**
     * @return 
     */
    @Override
    public Number getNumber() {
        throw new UnsupportedOperationException("Cannot call getNumber() on JsonStringNode");
    }

    /**
     * @return 
     */
    @Override
    public Boolean getBoolean() {
        throw new UnsupportedOperationException("Cannot call getBoolean() on JsonStringNode");
    }

    /**
     * @return
     */
    @Override
    public JsonObjectNode getObject() {
        throw new UnsupportedOperationException("Cannot call getObject() on JsonStringNode");
    }

    /**
     * @return 
     */
    @Override
    public JsonArrayNode getArray() {
        throw new UnsupportedOperationException("Cannot call getArray() on JsonStringNode");
    }

    /**
     * @param key 
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (!String.class.isAssignableFrom(clazz)) {
            throw new UnsupportedOperationException("Cannot call get(String, Class<" + clazz.getName() + ">) on JsonStringNode");
        }

        return clazz.cast(value);
    }
}
