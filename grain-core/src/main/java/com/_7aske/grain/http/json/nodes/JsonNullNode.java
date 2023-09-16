package com._7aske.grain.http.json.nodes;

public class JsonNullNode extends JsonNode {
    public static final JsonNullNode INSTANCE = new JsonNullNode();

    /**
     * @param key 
     * @return
     */
    @Override
    public JsonNode get(String key) {
        return null;
    }

    /**
     * @return 
     */
    @Override
    public Object getValue() {
        return null;
    }

    /**
     * @return 
     */
    @Override
    public String getString() {
        return null;
    }

    /**
     * @return 
     */
    @Override
    public Number getNumber() {
        return null;
    }

    /**
     * @return 
     */
    @Override
    public Boolean getBoolean() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public JsonObjectNode getObject() {
        return null;
    }

    /**
     * @return 
     */
    @Override
    public JsonArrayNode getArray() {
        return null;
    }

    /**
     * @param key 
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        return null;
    }
}
