package ru.clevertec.cache;

import java.util.Optional;

public interface CustomCache<K, V> {
    void put(K key, V value);

    Optional<V> get(K key);

    void delete(K key);
}
