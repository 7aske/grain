package com._7aske.grain.http.json.nodes;

public abstract class JsonNode {
    // String, Number, Boolean, Map<String, JsonNode>, List<JsonNode>
    protected Object value;


    protected JsonNode() {
    }

    protected JsonNode(Object value) {
        this.value = value;
    }

    public abstract JsonNode get(String key);

    /**
     * @return Raw value stored in the json object.
     */
    public abstract Object getValue();

    public abstract String getString();

    public abstract Number getNumber();

    public abstract Boolean getBoolean();

    public abstract JsonObjectNode getObject();

    public abstract JsonArrayNode getArray();

    public abstract <T> T get(String key, Class<T> clazz);
}
