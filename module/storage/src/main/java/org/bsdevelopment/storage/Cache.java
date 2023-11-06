package org.bsdevelopment.storage;

import com.google.common.base.Ticker;

import java.util.concurrent.TimeUnit;

/**
 * The Cache class provides a simple cache mechanism for storing and managing items with expiration times.
 * It allows setting an item to be cached along with its expiration time and checking if the cache is still valid.
 *
 * @param <E> The type of item to be cached.
 */
public class Cache<E> {
    private final Ticker ticker = Ticker.systemTicker();
    private long targetTime = -1;
    private E item;

    /**
     * Sets the item to be cached and specifies the time at which it will expire.
     *
     * @param item        The item to be cached.
     * @param expireDelay The amount of time to wait before the item expires.
     * @param expireUnit  The time unit of the expireDelay parameter.
     */
    public void setCacheItem(E item, long expireDelay, TimeUnit expireUnit) {
        targetTime = this.ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, expireUnit);
        this.item = item;
    }

    /**
     * Checks if the cached item is still valid. If the current time is greater than the target time, the item is considered
     * expired, and it becomes null. Otherwise, the item is not null.
     *
     * @return true if the item is valid and not expired; false if the item is null or has expired.
     */
    public boolean hasCacheItem() {
        long current = this.ticker.read();
        if (targetTime <= current) {
            item = null;
            targetTime = -1;
            return false;
        }

        return (item != null);
    }

    /**
     * Returns the item stored in the cache.
     *
     * @return The item that is currently cached, or null if no item is cached or the cached item has expired.
     */
    public E getCacheItem() {
        return item;
    }
}