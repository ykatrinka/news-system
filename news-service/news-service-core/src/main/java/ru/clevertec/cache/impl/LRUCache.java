package ru.clevertec.cache.impl;

import org.springframework.beans.factory.annotation.Value;
import ru.clevertec.cache.CustomCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LRUCache<K, V> implements CustomCache<K, V> {

    @Value("${cache.capacity}")
    private int capacity;
    private final Map<K, Node<K, V>> cacheList = new HashMap<>();
    private Node<K, V> head = new Node<>(null, null);
    private Node<K, V> tail = new Node<>(null, null);


    public LRUCache() {
        this.head.next = tail;
        this.tail.prev = head;
    }

    @Override
    public void put(K key, V value) {
        Node<K, V> node = cacheList.get(key);
        if (node != null) {
            node.value = value;
            removeNode(node);
            moveToHead(node);
        } else {
            node = new Node<>(key, value);
            cacheList.put(key, node);
            moveToHead(node);

            if (cacheList.size() > capacity) {
                cacheList.remove(tail.prev.key);
                removeNode(tail.prev);
            }
        }
    }

    @Override
    public Optional<V> get(K key) {
        Node<K, V> node = cacheList.get(key);
        if (node == null) {
            return Optional.empty();
        }

        removeNode(node);
        moveToHead(node);
        return Optional.ofNullable(node.value);
    }

    @Override
    public void delete(K key) {
        removeNode(cacheList.get(key));
        cacheList.remove(key);
    }

    private void removeNode(Node<K, V> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private void moveToHead(Node<K, V> node) {
        Node<K, V> tmp = head.next;

        if (tmp != null) {
            head.next = node;

            node.prev = head;
            node.next = tmp;

            tmp.prev = node;
        } else {
            node.prev = head.prev;
            node.next = head;

            head = node;
        }
    }

    private static class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> prev;
        private Node<K, V> next;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}