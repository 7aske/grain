package com._7aske.grain.web.http.codec.json;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.util.ReflectionUtil;
import com._7aske.grain.web.http.codec.json.annotation.JsonAlias;
import com._7aske.grain.web.http.codec.json.annotation.JsonIgnore;
import com._7aske.grain.web.http.codec.json.annotation.JsonProperty;
import com._7aske.grain.web.http.codec.json.nodes.*;

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
        return mapValue(parser.parse(json), clazz);
    }

    public JsonNode mapValue(Object value) {
        if (value == null) {
            return JsonNullNode.INSTANCE;
        } else if (value instanceof List<?> list) {
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

    public Object mapValue(String jsonString, Parameter param) {
        JsonParser parser = new JsonParser();
        return mapValue(parser.parse(jsonString), param);
    }

    public Object mapValue(JsonNode root, Parameter param) {
        return mapValue(root, param.getParameterizedType());
    }

    public Object mapValue(JsonNode root, Type type) {
        if (root == null || JsonNullNode.INSTANCE.equals(root)) {
            return null;
        }

        if (type instanceof ParameterizedType paramType) {

            Class<?> clazz = (Class<?>) paramType.getRawType();

            if (List.class.isAssignableFrom(clazz)) {
                return root.asArray().getStream()
                        .map(node -> mapValue(node, paramType.getActualTypeArguments()[0]))
                        .toList();
            } else if (Map.class.isAssignableFrom(clazz)) {
                return root.asObject().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapValue(entry.getValue(), paramType.getActualTypeArguments()[1])));
            } else if (Set.class.isAssignableFrom(clazz)) {
                return root.asArray().getStream()
                        .map(node -> mapValue(node, paramType.getActualTypeArguments()[0]))
                        .collect(Collectors.toSet());
            }

        } else if (type instanceof Class<?> clazz) {
            try {
                if (Object[].class.isAssignableFrom(clazz)) {
                    throw new GrainRuntimeException("Cannot Object[] parameters");
                }

                Constructor<?> constructor = ReflectionUtil.getAnyConstructor(clazz);
                Object instance = constructor.newInstance();

                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (ReflectionUtil.isAnnotationPresent(field, JsonIgnore.class)) {
                        continue;
                    }

                    if (field.getType().isPrimitive()) {
                        field.set(instance, getFieldValue(field, root).getValue());
                    } else if (String.class.isAssignableFrom(field.getType())) {
                        field.set(instance, getFieldValue(field, root).getString());
                    } else if (Number.class.isAssignableFrom(field.getType())) {
                        field.set(instance, getFieldValue(field, root).getNumber());
                    } else if (Boolean.class.isAssignableFrom(field.getType())) {
                        field.set(instance, getFieldValue(field, root).getBoolean());
                    } else if (Set.class.isAssignableFrom(field.getType())) {
                        JsonArrayNode array = getFieldValue(field, root).asArray();
                        field.set(instance, array.getStream()
                                .map(node -> mapValue(node, field.getType()))
                                .collect(Collectors.toSet()));
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        JsonArrayNode array = getFieldValue(field, root).asArray();
                        field.set(instance, array.getStream()
                                .map(node -> mapValue(node, field.getType()))
                                .toList());
                    } else {
                        field.set(instance, mapValue(getFieldValue(field, root), field.getType()));
                    }
                }
                return instance;

            } catch (NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                throw new GrainRuntimeException(e);
            }
        }

        throw new GrainRuntimeException("Cannot map value");
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
