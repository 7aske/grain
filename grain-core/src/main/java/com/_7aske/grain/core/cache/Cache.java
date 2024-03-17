package com._7aske.grain.core.cache;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Cache {
    String getName();

    Object get(CacheKey key);

    default Object getOrDefault(CacheKey key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    default <K extends CacheKey> Object computeIfAbsent(K key, Function<? super K, Object> remappingFunction) {
        Object value = get(key);
        if (value == null) {
            return put(key, remappingFunction.apply(key));
        }

        return value;
    }

    default <K extends CacheKey> Object computeIfPresent(K key, BiFunction<? super K, Object, Object> remappingFunction) {
        Object value = get(key);
        if (value != null) {
            return put(key, remappingFunction.apply(key, value));
        }

        return null;
    }

    Object put(CacheKey key, Object value);

    void evict(CacheKey key);

    void clear();

    int size();

    boolean contains(CacheKey key);

    boolean isEmpty();
}
