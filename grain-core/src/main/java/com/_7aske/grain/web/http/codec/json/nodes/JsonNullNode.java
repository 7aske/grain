package com._7aske.grain.web.http.codec.json.nodes;

public class JsonNullNode extends JsonNode {
    public static final JsonNullNode INSTANCE = new JsonNullNode();

    @Override
    public JsonObjectNode asObject() {
        return null;
    }

    @Override
    public JsonArrayNode asArray() {
        return null;
    }
}
