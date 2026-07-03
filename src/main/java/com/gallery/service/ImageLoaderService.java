package com.gallery.service;

import com.gallery.model.ImageFile;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for loading full-resolution images asynchronously.
 * 
 * WHY: Loading high-resolution images can be slow and memory-intensive.
 * This service loads images on background threads to prevent UI freezing,
 * especially important when dealing with large images (10MB+).
 */
public class ImageLoaderService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImageLoaderService.class);
    
    /**
     * Executor service for background image loading.
     * Uses fewer threads than thumbnail service since image loading is more I/O bound.
     */
    private final ExecutorService executor;
    
    /**
     * Creates an ImageLoaderService with default settings.
     */
    public ImageLoaderService() {
        this(2);
    }
    
    /**
     * Creates an ImageLoaderService with custom thread count.
     * 
     * @param threadCount Number of background threads for loading
     */
    public ImageLoaderService(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r);
            t.setName("Image-Loader-" + t.getId());
            t.setDaemon(true);
            return t;
        });
        LOG.info("ImageLoaderService initialized with {} threads", threadCount);
    }
    
    /**
     * Loads a full-resolution image asynchronously.
     * 
     * @param imageFile The image file to load
     * @return CompletableFuture containing the loaded Image
     */
    public CompletableFuture<Image> loadImageAsync(ImageFile imageFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadImage(imageFile.getPath());
            } catch (IOException e) {
                LOG.error("Failed to load image: {}", imageFile.getPath(), e);
                return null;
            }
        }, executor);
    }
    
    /**
     * Loads a full-resolution image synchronously.
     * Should only be called from background threads.
     * 
     * @param path Path to the image file
     * @return The loaded Image
     * @throws IOException If loading fails
     */
    public Image loadImage(Path path) throws IOException {
        LOG.debug("Loading image: {}", path);
        
        if (!Files.exists(path)) {
            throw new IOException("Image file does not exist: " + path);
        }
        
        // Load image with JavaFX Image class
        // preserveRatio and smooth are set for better display quality
        String imageUrl = path.toUri().toString();
        Image image = new Image(imageUrl);
        
        // Wait for image to fully load (necessary for large images)
        if (image.isError()) {
            throw new IOException("Failed to load image: " + image.getException());
        }
        
        LOG.debug("Successfully loaded image: {} ({}x{})", 
                  path, image.getWidth(), image.getHeight());
        
        return image;
    }
    
    /**
     * Loads an image with specified dimensions for memory efficiency.
     * Useful for fitting images to screen without loading full resolution.
     * 
     * @param path Path to the image file
     * @param fitWidth Target width
     * @param fitHeight Target height
     * @return The loaded Image scaled to fit
     * @throws IOException If loading fails
     */
    public Image loadImageFit(Path path, double fitWidth, double fitHeight) throws IOException {
        LOG.debug("Loading image with fit dimensions: {} ({}x{})", path, fitWidth, fitHeight);
        
        if (!Files.exists(path)) {
            throw new IOException("Image file does not exist: " + path);
        }
        
        String imageUrl = path.toUri().toString();
        Image image = new Image(imageUrl, fitWidth, fitHeight, true, true);
        
        if (image.isError()) {
            throw new IOException("Failed to load image: " + image.getException());
        }
        
        return image;
    }
    
    /**
     * Preloads multiple images in the background.
     * Useful for preloading adjacent images in a viewer.
     * 
     * @param imageFiles List of image files to preload
     */
    public void preloadImages(java.util.List<ImageFile> imageFiles) {
        for (ImageFile imageFile : imageFiles) {
            loadImageAsync(imageFile).thenAccept(image -> {
                if (image != null) {
                    LOG.trace("Preloaded image: {}", imageFile.getPath());
                }
            });
        }
    }
    
    /**
     * Shuts down the executor service.
     * Call this when the application is closing.
     */
    public void shutdown() {
        executor.shutdown();
        LOG.info("ImageLoaderService shut down");
    }
}
