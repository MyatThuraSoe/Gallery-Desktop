package com.gallery.exception;

/**
 * Exception thrown when a cache operation fails.
 * 
 * This exception covers failures in memory or disk cache operations:
 * - Cache initialization failure
 * - Cache write failure (disk full, permission issues)
 * - Cache read failure (corrupted cache entry)
 * - Cache eviction errors
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class CacheException extends GalleryException {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** The cache key involved in the failed operation (if applicable) */
    private final String cacheKey;

    /** The type of cache (MEMORY or DISK) */
    private final String cacheType;

    /**
     * Constructs a new CacheException with the specified detail message,
     * cache type, and cache key.
     * 
     * @param message   The detail message explaining the exception
     * @param cacheType The type of cache where the error occurred
     * @param cacheKey  The cache key involved (may be null)
     */
    public CacheException(String message, String cacheType, String cacheKey) {
        super(message, "CACHE_OPERATION_FAILED");
        this.cacheType = cacheType;
        this.cacheKey = cacheKey;
    }

    /**
     * Constructs a new CacheException with the specified detail message,
     * cache type, cache key, and underlying cause.
     * 
     * @param message   The detail message explaining the exception
     * @param cacheType The type of cache where the error occurred
     * @param cacheKey  The cache key involved (may be null)
     * @param cause     The underlying cause of this exception
     */
    public CacheException(String message, String cacheType, String cacheKey, Throwable cause) {
        super(message, cause, "CACHE_OPERATION_FAILED");
        this.cacheType = cacheType;
        this.cacheKey = cacheKey;
    }

    /**
     * Gets the cache key involved in the failed operation.
     * May be null if the error is not related to a specific key.
     * 
     * @return The cache key as a String, or null
     */
    public String getCacheKey() {
        return cacheKey;
    }

    /**
     * Gets the type of cache where the error occurred.
     * Common values: "MEMORY", "DISK"
     * 
     * @return The cache type as a String
     */
    public String getCacheType() {
        return cacheType;
    }

    /**
     * Returns a formatted string representation of this exception.
     * 
     * @return Formatted exception details
     */
    @Override
    public String toString() {
        String keyInfo = (cacheKey != null) ? ", key: " + cacheKey : "";
        return String.format(
            "CacheException [%s] at %d: %s (type: %s%s)",
            getErrorCode(),
            getTimestamp(),
            getMessage(),
            cacheType,
            keyInfo
        );
    }
}
