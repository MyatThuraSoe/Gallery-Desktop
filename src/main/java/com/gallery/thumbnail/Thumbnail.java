package com.gallery.thumbnail;

import javafx.scene.image.Image;

/**
 * Represents a cached thumbnail with metadata.
 * 
 * Immutable data class for thread-safe operations.
 */
public record Thumbnail(
    String imagePath,
    Image image,
    int width,
    int height,
    long fileSize,
    long timestamp
) {
    
    /**
     * Creates a new Thumbnail with current timestamp.
     */
    public static Thumbnail create(String imagePath, Image image, int width, int height, long fileSize) {
        return new Thumbnail(imagePath, image, width, height, fileSize, System.currentTimeMillis());
    }
}
