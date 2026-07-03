package com.gallery.dto;

import com.gallery.model.ImageFile;
import javafx.scene.image.Image;

/**
 * ThumbnailDTO - Data Transfer Object for thumbnail generation results.
 * 
 * WHY: Decouples thumbnail generation from UI components.
 * Provides a clean interface between service layer and view layer.
 * Follows DTO pattern for efficient data transfer.
 */
public record ThumbnailDTO(
    String imagePath,
    Image image,
    int width,
    int height,
    long timestamp
) {
    public static ThumbnailDTO create(ImageFile imageFile, Image thumbnail) {
        return new ThumbnailDTO(
            imageFile.getPath().toString(),
            thumbnail,
            (int)thumbnail.getWidth(),
            (int)thumbnail.getHeight(),
            System.currentTimeMillis()
        );
    }
}
