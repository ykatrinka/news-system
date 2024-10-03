package ru.clevertec.cache.factory;

import ru.clevertec.cache.CustomCache;

public interface CacheFactory<K, V> {
    CustomCache<K, V> getInstance(CacheType type);
}
