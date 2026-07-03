package com.gallery.cache;

import com.gallery.thumbnail.Thumbnail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LRU (Least Recently Used) cache for thumbnails using memory.
 * 
 * WHY: To avoid regenerating thumbnails and reduce disk I/O, we cache them in memory.
 * This implementation is thread-safe and automatically evicts old entries when capacity is reached.
 * 
 * @param <K> Key type (typically image path as String)
 * @param <V> Value type (Thumbnail)
 */
public class ThumbnailCache<K, V> {
    
    private static final Logger LOG = LoggerFactory.getLogger(ThumbnailCache.class);
    
    /**
     * Default maximum number of cached thumbnails.
     * Adjust based on available memory (each thumbnail ~50-100KB).
     */
    private static final int DEFAULT_CAPACITY = 500;
    
    /**
     * Read-write lock for thread-safe operations.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Internal LRU cache implementation using LinkedHashMap.
     */
    private final Map<K, V> cache;
    
    /**
     * Cache hit counter for statistics.
     */
    private long hits = 0;
    
    /**
     * Cache miss counter for statistics.
     */
    private long misses = 0;
    
    /**
     * Creates a cache with default capacity.
     */
    public ThumbnailCache() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates a cache with specified capacity.
     * 
     * @param capacity Maximum number of entries
     */
    public ThumbnailCache(int capacity) {
        this.cache = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > capacity;
                if (shouldRemove) {
                    LOG.trace("Evicting eldest entry from cache: {}", eldest.getKey());
                }
                return shouldRemove;
            }
        };
        LOG.info("ThumbnailCache initialized with capacity: {}", capacity);
    }
    
    /**
     * Retrieves a thumbnail from the cache.
     * 
     * @param key The image path
     * @return Optional containing the thumbnail if found
     */
    public Optional<V> get(K key) {
        lock.readLock().lock();
        try {
            V value = cache.get(key);
            if (value != null) {
                hits++;
                LOG.trace("Cache hit for: {}", key);
            } else {
                misses++;
                LOG.trace("Cache miss for: {}", key);
            }
            return Optional.ofNullable(value);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Adds a thumbnail to the cache.
     * 
     * @param key The image path
     * @param value The thumbnail
     */
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
            LOG.trace("Cached thumbnail for: {} (size: {})", key, cache.size());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Removes a thumbnail from the cache.
     * 
     * @param key The image path
     * @return true if removed successfully
     */
    public boolean remove(K key) {
        lock.writeLock().lock();
        try {
            boolean removed = cache.remove(key) != null;
            if (removed) {
                LOG.trace("Removed thumbnail from cache: {}", key);
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            hits = 0;
            misses = 0;
            LOG.info("Cache cleared");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Returns the current number of cached entries.
     * 
     * @return Cache size
     */
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Returns the cache capacity.
     * 
     * @return Maximum capacity
     */
    public int getCapacity() {
        return cache instanceof LinkedHashMap ? 
            ((LinkedHashMap<K, V>) cache).size() : DEFAULT_CAPACITY;
    }
    
    /**
     * Gets cache statistics (hit rate).
     * 
     * @return Hit rate as percentage (0.0 - 100.0)
     */
    public double getHitRate() {
        long total = hits + misses;
        if (total == 0) {
            return 0.0;
        }
        return (double) hits / total * 100.0;
    }
    
    /**
     * Checks if the cache contains a key.
     * 
     * @param key The image path
     * @return true if present
     */
    public boolean containsKey(K key) {
        lock.readLock().lock();
        try {
            return cache.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }
}
