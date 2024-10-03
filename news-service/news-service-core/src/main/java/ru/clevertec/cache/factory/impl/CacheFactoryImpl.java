package ru.clevertec.cache.factory.impl;

import ru.clevertec.cache.CustomCache;
import ru.clevertec.cache.factory.CacheFactory;
import ru.clevertec.cache.factory.CacheType;
import ru.clevertec.cache.impl.LFUCache;
import ru.clevertec.cache.impl.LRUCache;
import ru.clevertec.exception.UnsupportCacheException;

public class CacheFactoryImpl<K, V> implements CacheFactory<K, V> {
    @Override
    public CustomCache<K, V> getInstance(CacheType type) {
        return switch (type) {
            case LFU -> new LFUCache<>();
            case LRU -> new LRUCache<>();
            default -> throw UnsupportCacheException.getByType();
        };
    }
}
