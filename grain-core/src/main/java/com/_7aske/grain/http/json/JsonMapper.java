package com._7aske.grain.http.json;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.http.json.annotation.JsonAlias;
import com._7aske.grain.http.json.annotation.JsonIgnore;
import com._7aske.grain.http.json.annotation.JsonProperty;
import com._7aske.grain.http.json.nodes.*;
import com._7aske.grain.util.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Grain
public class JsonMapper {

    public String stringifyValue(JsonNode root, boolean pretty, int indent) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(pretty, indent);
        return jsonWriter.write(root);
    }

    public String stringifyValue(JsonNode root, boolean pretty) throws IOException {
        return stringifyValue(root, pretty, 2);
    }

    public String stringifyValue(JsonNode root) throws IOException {
        return stringifyValue(root, false);
    }

    public <T> T parseValue(String json, Class<T> clazz) {
        JsonParser parser = new JsonParser();
        return mapValue(parser.parse(json), clazz);
    }

    public JsonNode mapValue(Object value) {
        if (value == null) return JsonNullNode.INSTANCE;

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(this::mapValue)
                    .collect(Collectors.toCollection(JsonArrayNode::new));
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

    public <T> T mapValue(JsonNode root, Class<T> clazz) {
        if (root == null) return null;

        try {
            Constructor<T> constructor = ReflectionUtil.getAnyConstructor(clazz);
            T instance = constructor.newInstance();


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
                            .map(node -> mapValue(node, field.getType()))
                            .toArray());
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