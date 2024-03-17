package com._7aske.grain.core.cache;

public interface CacheKeyGenerator {
    CacheKey generate(Object... args);
}
