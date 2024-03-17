package com._7aske.grain.core.cache;

public interface CacheManager {
    Cache createCache(String name);

    void addCache(Cache cache);

    Cache getCache(String name);

    void removeCache(String name);

    void clearCache(String name);

    void clearAllCaches();
}
