package com._7aske.grain.http.json.nodes;

import java.util.ArrayList;
import java.util.List;

public class JsonArrayNode extends JsonNode {
    public JsonArrayNode() {
        super(new ArrayList<JsonNode>());
    }

    public JsonNode get(int index) {
        return ((List<JsonNode>)value).get(index);
    }

    public void add(JsonNode value) {
        ((List<JsonNode>) this.value).add(value);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public JsonNode get(String key) {
        throw new UnsupportedOperationException("Cannot call get(String) on JsonArrayNode");
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
        throw new UnsupportedOperationException("Cannot call getString() on JsonArrayNode");
    }

    /**
     * @return
     */
    @Override
    public Number getNumber() {
        throw new UnsupportedOperationException("Cannot call getNumber() on JsonArrayNode");
    }

    /**
     * @return
     */
    @Override
    public Boolean getBoolean() {
        throw new UnsupportedOperationException("Cannot call getBoolean() on JsonArrayNode");
    }

    /**
     * @return
     */
    @Override
    public JsonObjectNode getObject() {
        throw new UnsupportedOperationException("Cannot call getObject() on JsonArrayNode");
    }

    /**
     * @return
     */
    @Override
    public JsonArrayNode getArray() {
        return this;
    }

    /**
     * @return
     */

    /**
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        throw new UnsupportedOperationException("Cannot call get(String, Class) on JsonArrayNode");
    }
}
