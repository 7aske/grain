package com._7aske.grain.http.json;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.http.json.annotation.JsonAlias;
import com._7aske.grain.http.json.annotation.JsonIgnore;
import com._7aske.grain.http.json.annotation.JsonProperty;
import com._7aske.grain.http.json.nodes.*;
import com._7aske.grain.util.ReflectionUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Grain
public class JsonMapper {
    private static final int DEFAULT_INDENT = 2;
    private static final boolean DEFAULT_PRETTY_PRINT = false;
    private int indentSize;
    private boolean prettyPrint;

    public JsonMapper() {
        this(DEFAULT_INDENT, DEFAULT_PRETTY_PRINT);
    }

    public JsonMapper(JsonMapper jsonMapper) {
        this.indentSize = jsonMapper.indentSize;
        this.prettyPrint = jsonMapper.prettyPrint;
    }

    public JsonMapper(int indentSize) {
        this(indentSize, DEFAULT_PRETTY_PRINT);
    }

    public JsonMapper(boolean prettyPrint) {
        this(DEFAULT_INDENT, prettyPrint);
    }

    public JsonMapper(int indentSize, boolean prettyPrint) {
        this.indentSize = indentSize;
        this.prettyPrint = prettyPrint;
    }

    public JsonMapper withIndent(int indentSize) {
        JsonMapper jsonMapper = new JsonMapper(this);
        jsonMapper.indentSize = indentSize;
        return jsonMapper;
    }

    public JsonMapper withPrettyPrint(boolean prettyPrint) {
        JsonMapper jsonMapper = new JsonMapper(this);
        jsonMapper.prettyPrint = prettyPrint;
        return jsonMapper;
    }

    public void writeValue(JsonNode root, OutputStream outputStream, boolean pretty, int indent) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(pretty, indent);
        jsonWriter.write(root, outputStream);
    }

    public void writeValue(JsonNode root, OutputStream outputStream, boolean pretty) throws IOException {
        writeValue(root, outputStream, pretty, indentSize);
    }

    public void writeValue(JsonNode root, OutputStream outputStream) throws IOException {
        writeValue(root, outputStream, prettyPrint);
    }

    public void writeValue(Object object, OutputStream outputStream) throws IOException {
        writeValue(mapValue(object), outputStream, prettyPrint);
    }

    public String stringifyValue(JsonNode root, boolean pretty, int indent) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(pretty, indent);
        return jsonWriter.write(root);
    }

    public String stringifyValue(JsonNode root, boolean pretty) throws IOException {
        return stringifyValue(root, pretty, indentSize);
    }

    public String stringifyValue(JsonNode root) throws IOException {
        return stringifyValue(root, prettyPrint);
    }

    public Object parseValue(String json, Class<?> clazz) {
        JsonParser parser = new JsonParser();
        return mapValue(parser.parse(json), clazz, false);
    }

    public Object parseValue(String json, Parameter param) {
        JsonParser parser = new JsonParser();
        boolean isList = List.class.isAssignableFrom(param.getType());
        Class<?> type = isList
                ? ReflectionUtil.getGenericListTypeArgument(param)
                : param.getType();
        return mapValue(parser.parse(json), type, isList);
    }

    public JsonNode mapValue(Object value) {
        if (value == null) return JsonNullNode.INSTANCE;

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(this::mapValue)
                    .collect(Collectors.toCollection(JsonArrayNode::new));
        } else if (value instanceof Map<?, ?> map) {
            JsonObjectNode object = new JsonObjectNode();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                object.put(entry.getKey().toString(), mapValue(entry.getValue()));
            }
            return object;
        } else if (value instanceof Set<?> set) {
            return set.stream()
                    .map(this::mapValue)
                    .collect(Collectors.toCollection(JsonArrayNode::new));
        } else if (Object[].class.isAssignableFrom(value.getClass())) {
            JsonArrayNode array = new JsonArrayNode();
            for (Object o : (Object[]) value) {
                array.add(mapValue(o));
            }
            return array;
        } else if (value instanceof String str) {
            return new JsonStringNode(str);
        } else if (value instanceof Number num) {
            return new JsonNumberNode(num);
        } else if (value instanceof Boolean bool) {
            return new JsonBooleanNode(bool);
        } else {
            JsonObjectNode object = new JsonObjectNode();
            for (Field field : value.getClass().getDeclaredFields()) {
                if (ReflectionUtil.isAnnotationPresent(field, JsonIgnore.class)) {
                    continue;
                }

                JsonNode mapped = mapValue(ReflectionUtil.getFieldValue(field, value));
                object.put(getFieldName(field), mapped);
            }

            return object;
        }
    }

    public Object mapValue(JsonNode root, Class<?> clazz, boolean isList) {
        if (root == null) return null;

        try {

            if (isList) {
                // @Warning won't work with nested lists
                return root.asArray().stream()
                        .map(node -> mapValue(node, clazz, List.class.isAssignableFrom(clazz)))
                        .toList();
            } else {
                Constructor<?> constructor = ReflectionUtil.getAnyConstructor(clazz);
                Object instance = constructor.newInstance();

                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (ReflectionUtil.isAnnotationPresent(field, JsonIgnore.class)) {
                        continue;
                    }

                    if (String.class.isAssignableFrom(field.getType())) {
                        field.set(instance, getFieldValue(field, root).getString());
                    } else if (Number.class.isAssignableFrom(field.getType())) {
                        field.set(instance, getFieldValue(field, root).getNumber());
                    } else if (Boolean.class.isAssignableFrom(field.getType())) {
                        field.set(instance, getFieldValue(field, root).getBoolean());
                    } else if (Object[].class.isAssignableFrom(field.getType())) {
                        JsonArrayNode array = getFieldValue(field, root).asArray();
                        field.set(instance, array.getStream()
                                .map(node -> mapValue(node, field.getType(), false))
                                .toArray());
                    } else if (Set.class.isAssignableFrom(field.getType())) {
                        JsonArrayNode array = getFieldValue(field, root).asArray();
                        field.set(instance, array.getStream()
                                .map(node -> mapValue(node, field.getType(), false))
                                .collect(Collectors.toSet()));
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        JsonArrayNode array = getFieldValue(field, root).asArray();
                        field.set(instance, array.getStream()
                                .map(node -> mapValue(node, field.getType(), false))
                                .toList());
                    } else {
                        field.set(instance, mapValue(getFieldValue(field, root), field.getType(), false));
                    }

                }

                return instance;
            }


        } catch (NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new GrainRuntimeException(e);
        }
    }

    private String getFieldName(Field field) {
        if (field.isAnnotationPresent(JsonProperty.class)) {
            return field.getAnnotation(JsonProperty.class).value();
        }

        return field.getName();
    }

    private JsonNode getFieldValue(Field field, JsonNode root) {
        if (field.isAnnotationPresent(JsonAlias.class)) {
            JsonAlias alias = field.getAnnotation(JsonAlias.class);

            for (String name : alias.value()) {
                if (root.asObject().containsKey(name)) {
                    return root.get(name);
                }
            }
        }

        return root.get(field.getName());
    }
}
