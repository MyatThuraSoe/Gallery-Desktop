package com.gallery.service;

import com.gallery.cache.ThumbnailCache;
import com.gallery.model.ImageFile;
import com.gallery.thumbnail.Thumbnail;
import com.gallery.thumbnail.ThumbnailGenerator;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for loading and caching thumbnails asynchronously.
 * 
 * WHY: Thumbnail loading must happen on background threads to prevent UI freezing.
 * This service coordinates thumbnail generation, caching, and async delivery to the UI.
 */
public class ThumbnailService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ThumbnailService.class);
    
    /**
     * Cache for storing generated thumbnails in memory.
     */
    private final ThumbnailCache<String, Thumbnail> cache;
    
    /**
     * Generator for creating thumbnails from image files.
     */
    private final ThumbnailGenerator generator;
    
    /**
     * Executor service for background thumbnail generation.
     * Uses a fixed thread pool to control resource usage.
     */
    private final ExecutorService executor;
    
    /**
     * Default thumbnail size.
     */
    private final int thumbnailSize;
    
    /**
     * Creates a ThumbnailService with default settings.
     */
    public ThumbnailService() {
        this(256, 500, 4);
    }
    
    /**
     * Creates a ThumbnailService with custom configuration.
     * 
     * @param thumbnailSize Size of thumbnails in pixels
     * @param cacheCapacity Maximum number of cached thumbnails
     * @param threadCount Number of background threads for generation
     */
    public ThumbnailService(int thumbnailSize, int cacheCapacity, int threadCount) {
        this.thumbnailSize = thumbnailSize;
        this.cache = new ThumbnailCache<>(cacheCapacity);
        this.generator = new ThumbnailGenerator();
        this.executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r);
            t.setName("Thumbnail-Generator-" + t.getId());
            t.setDaemon(true);
            return t;
        });
        LOG.info("ThumbnailService initialized (size: {}, cache: {}, threads: {})", 
                 thumbnailSize, cacheCapacity, threadCount);
    }
    
    /**
     * Loads a thumbnail asynchronously.
     * Returns immediately with a CompletableFuture that completes when the thumbnail is ready.
     * 
     * @param imageFile The image file to generate thumbnail for
     * @return CompletableFuture containing the thumbnail
     */
    public CompletableFuture<Thumbnail> loadThumbnailAsync(ImageFile imageFile) {
        return CompletableFuture.supplyAsync(() -> {
            return getOrCreateThumbnail(imageFile);
        }, executor);
    }
    
    /**
     * Gets a thumbnail from cache or generates it if not present.
     * 
     * @param imageFile The image file
     * @return The Thumbnail, or null if generation fails
     */
    private Thumbnail getOrCreateThumbnail(ImageFile imageFile) {
        String path = imageFile.getPath().toString();
        
        // Check cache first
        return cache.get(path).orElseGet(() -> {
            try {
                // Generate new thumbnail
                byte[] data = generator.generateSquareThumbnail(imageFile.getPath(), thumbnailSize);
                Image fxImage = new Image(new ByteArrayInputStream(data));
                
                // Get dimensions
                int[] dims = generator.getImageDimensions(imageFile.getPath());
                
                // Create thumbnail object
                Thumbnail thumbnail = Thumbnail.create(
                    path,
                    fxImage,
                    dims[0],
                    dims[1],
                    imageFile.getFileSize()
                );
                
                // Cache it
                cache.put(path, thumbnail);
                
                LOG.debug("Generated and cached thumbnail for: {}", path);
                return thumbnail;
                
            } catch (IOException e) {
                LOG.error("Error generating thumbnail for: {}", path, e);
                return null;
            }
        });
    }
    
    /**
     * Preloads multiple thumbnails in the background.
     * Useful for preloading visible items in a grid view.
     * 
     * @param imageFiles List of image files to preload
     */
    public void preloadThumbnails(java.util.List<ImageFile> imageFiles) {
        for (ImageFile imageFile : imageFiles) {
            // Start async loading but don't wait for completion
            loadThumbnailAsync(imageFile).thenAccept(thumbnail -> {
                if (thumbnail != null) {
                    LOG.trace("Preloaded thumbnail for: {}", imageFile.getPath());
                }
            });
        }
    }
    
    /**
     * Gets a cached thumbnail synchronously.
     * Returns immediately without generating if not in cache.
     * 
     * @param imagePath Path to the image
     * @return Optional containing the cached thumbnail
     */
    public java.util.Optional<Thumbnail> getCachedThumbnail(String imagePath) {
        return cache.get(imagePath);
    }
    
    /**
     * Clears all cached thumbnails.
     */
    public void clearCache() {
        cache.clear();
        LOG.info("Thumbnail cache cleared");
    }
    
    /**
     * Gets cache statistics.
     * 
     * @return Array with [size, capacity, hitRate]
     */
    public double[] getCacheStats() {
        return new double[] {
            cache.size(),
            cache.getCapacity(),
            cache.getHitRate()
        };
    }
    
    /**
     * Shuts down the executor service.
     * Call this when the application is closing.
     */
    public void shutdown() {
        executor.shutdown();
        LOG.info("ThumbnailService shut down");
    }
}
