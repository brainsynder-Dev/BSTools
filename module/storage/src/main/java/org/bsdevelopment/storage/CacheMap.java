package org.bsdevelopment.storage;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * The `CacheMap` class provides a simple in-memory cache with key-value pairs that expire after a specified time.
 *
 * @param <K> The type of keys.
 * @param <V> The type of values to be stored in the cache.
 */
public class CacheMap<K, V> {
    private final Map<K, ExpireEntry> keyLookup;
    private final PriorityQueue<ExpireEntry> expireQueue;
    private final Map<K, V> valueView;
    private final Ticker ticker;

    /**
     * Creates a new `CacheMap` instance using the system ticker.
     */
    public CacheMap() {
        this(Ticker.systemTicker());
    }

    /**
     * Creates a new `CacheMap` instance using a custom `Ticker`.
     *
     * @param ticker The ticker used for tracking time.
     */
    public CacheMap(Ticker ticker) {
        this.keyLookup = new HashMap();
        this.expireQueue = new PriorityQueue();
        this.valueView = Maps.transformValues(this.keyLookup, new Function<ExpireEntry, V>() {
            public V apply(CacheMap<K, V>.ExpireEntry entry) {
                return entry.value;
            }
        });
        this.ticker = ticker;
    }

    /**
     * Retrieves a value from the cache associated with the given key. If the key is not found in the cache or has expired, null is returned.
     *
     * @param key The key for which to retrieve the associated value.
     * @return The value associated with the key, or null if not found or expired.
     */
    public V get(K key) {
        this.evict();
        CacheMap.ExpireEntry entry = this.keyLookup.get(key);
        return entry != null ? (V) entry.value : null;
    }

    /**
     * Adds or updates a key-value pair in the cache with an expiration time.
     *
     * @param key         The key to be associated with the value.
     * @param value       The value to be stored in the cache.
     * @param expireDelay The amount of time to wait before the key-value pair expires.
     * @param expireUnit  The time unit of the expireDelay parameter.
     * @return The previous value associated with the key, or null if the key is new.
     */
    public V put(K key, V value, long expireDelay, TimeUnit expireUnit) {
        Preconditions.checkNotNull(expireUnit, "expireUnit cannot be NULL");
        Preconditions.checkState(expireDelay > 0L, "expireDelay cannot be equal or less than zero.");
        this.evict();
        CacheMap.ExpireEntry entry = new CacheMap.ExpireEntry(this.ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, expireUnit), key, value);
        CacheMap.ExpireEntry previous = this.keyLookup.put(key, entry);
        this.expireQueue.add(entry);
        return previous != null ? (V) previous.value : null;
    }

    /**
     * Checks if the cache contains the given key.
     *
     * @param key The key to be checked for existence in the cache.
     * @return true if the cache contains the key, otherwise false.
     */
    public boolean containsKey(K key) {
        this.evict();
        return this.keyLookup.containsKey(key);
    }

    /**
     * Checks if the cache contains the given value.
     *
     * @param value The value to be checked for existence in the cache.
     * @return true if the cache contains the value, otherwise false.
     */
    public boolean containsValue(V value) {
        this.evict();
        Iterator var2 = this.keyLookup.values().iterator();

        CacheMap.ExpireEntry entry;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            entry = (CacheMap.ExpireEntry) var2.next();
        } while (!Objects.equal(value, entry.value));

        return true;
    }

    /**
     * Removes a key-value pair from the cache using the provided key.
     *
     * @param key The key for the key-value pair to be removed.
     * @return The value associated with the key, or null if the key is not found.
     */
    public V removeKey(K key) {
        this.evict();
        CacheMap.ExpireEntry entry = this.keyLookup.remove(key);
        return entry != null ? (V) entry.value : null;
    }

    /**
     * Retrieves the current size of the cache.
     *
     * @return The number of key-value pairs currently stored in the cache.
     */
    public int size() {
        this.evict();
        return this.keyLookup.size();
    }

    /**
     * Retrieves a set of keys in the cache.
     *
     * @return A set of keys currently stored in the cache.
     */
    public Set<K> keySet() {
        this.evict();
        return this.keyLookup.keySet();
    }

    /**
     * Retrieves a collection of values in the cache.
     *
     * @return A collection of values currently stored in the cache.
     */
    public Collection<V> values() {
        this.evict();
        return this.valueView.values();
    }

    /**
     * Retrieves a set of key-value pairs as entries in the cache.
     *
     * @return A set of key-value pairs currently stored in the cache.
     */
    public Set<Entry<K, V>> entrySet() {
        this.evict();
        return this.valueView.entrySet();
    }

    /**
     * Retrieves the cache as a map view.
     *
     * @return A map view of the cache.
     */
    public Map<K, V> asMap() {
        this.evict();
        return this.valueView;
    }

    /**
     * Forces a collection of expired items in the cache.
     */
    public void collect() {
        this.evict();
        this.expireQueue.clear();
        this.expireQueue.addAll(this.keyLookup.values());
    }

    /**
     * Clears all items from the cache.
     */
    public void clear() {
        this.keyLookup.clear();
        this.expireQueue.clear();
    }

    /**
     * Evicts expired items from the cache.
     */
    protected void evict() {
        long current = this.ticker.read();

        while (this.expireQueue.size() > 0 && this.expireQueue.peek().time <= current) {
            CacheMap.ExpireEntry entry = this.expireQueue.poll();
            if (entry == this.keyLookup.get(entry.key)) {
                this.keyLookup.remove(entry.key);
            }
        }
    }

    @Override
    public String toString() {
        return keyLookup.toString();
    }

    /**
     * The inner class `ExpireEntry` represents an entry in the cache with an associated expiration time.
     */
    private class ExpireEntry implements Comparable<ExpireEntry> {
        public final long time;
        public final K key;
        public final V value;

        public ExpireEntry(long time, K key, V value) {
            this.time = time;
            this.key = key;
            this.value = value;
        }

        public int compareTo(CacheMap<K, V>.ExpireEntry o) {
            return Longs.compare(this.time, o.time);
        }

        @Override
        public String toString() {
            return "ExpireEntry [time=" + this.time + ", key=" + this.key + ", value=" + this.value + "]";
        }
    }
}
