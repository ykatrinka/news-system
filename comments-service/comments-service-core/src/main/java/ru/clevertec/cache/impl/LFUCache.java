package ru.clevertec.cache.impl;

import org.springframework.beans.factory.annotation.Value;
import ru.clevertec.cache.CustomCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LFUCache<K, V> implements CustomCache<K, V> {

    @Value("${cache.capacity}")
    private int capacity;
    private final Map<K, V> cacheList = new HashMap<>();
    private final Map<K, Integer> frequencyMap = new HashMap<>();

    @Override
    public void put(K key, V value) {
        if (!cacheList.containsKey(key) && cacheList.size() == capacity) {
            removeOldest();
        }

        cacheList.put(key, value);
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
    }

    @Override
    public Optional<V> get(K key) {
        if (!cacheList.containsKey(key)) {
            return Optional.empty();
        }

        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        return Optional.ofNullable(cacheList.get(key));
    }

    @Override
    public void delete(K key) {
        cacheList.remove(key);
        frequencyMap.remove(key);
    }

    private void removeOldest() {
        Optional<K> removedKey = frequencyMap.values().stream()
                .min(Integer::compareTo)
                .flatMap(min -> cacheList.entrySet().stream()
                        .filter(entry -> entry.getValue() == min)
                        .map(Map.Entry::getKey)
                        .findFirst());

        removedKey.ifPresent(cacheList::remove);
        removedKey.ifPresent(frequencyMap::remove);
    }

}
