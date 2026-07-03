package com.gallery.metadata;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Immutable record representing extracted image metadata.
 * 
 * This record follows the immutability principle to ensure thread-safety
 * and prevent accidental modification of metadata after extraction.
 * 
 * @author Gallery Team
 * @version 1.0
 */
public record Metadata(
    // Camera Information
    String cameraMake,
    String cameraModel,
    String lensModel,
    
    // Exposure Settings
    Double iso,
    Double exposureTime,
    Double fNumber,
    Double focalLength,
    
    // Image Properties
    Integer width,
    Integer height,
    Integer bitDepth,
    String colorProfile,
    String orientation,
    
    // Date & Time
    LocalDateTime dateTaken,
    LocalDateTime dateModified,
    
    // GPS Information
    Double latitude,
    Double longitude,
    Double altitude,
    
    // File Information
    Long fileSize,
    String mimeType,
    String fileExtension
) {
    
    /**
     * Creates a builder for constructing Metadata instances.
     * 
     * @return a new MetadataBuilder instance
     */
    public static MetadataBuilder builder() {
        return new MetadataBuilder();
    }
    
    /**
     * Checks if GPS information is available.
     * 
     * @return true if latitude and longitude are present
     */
    public boolean hasGpsInfo() {
        return latitude != null && longitude != null;
    }
    
    /**
     * Checks if camera information is available.
     * 
     * @return true if camera make or model is present
     */
    public boolean hasCameraInfo() {
        return cameraMake != null || cameraModel != null;
    }
    
    /**
     * Gets the resolution as a formatted string (e.g., "1920x1080").
     * 
     * @return resolution string or "Unknown" if dimensions are missing
     */
    public String getResolutionString() {
        if (width == null || height == null) {
            return "Unknown";
        }
        return width + " x " + height;
    }
    
    /**
     * Calculates the total megapixels.
     * 
     * @return megapixels rounded to 2 decimal places, or null if dimensions missing
     */
    public Double getMegapixels() {
        if (width == null || height == null) {
            return null;
        }
        return Math.round((width * height / 1_000_000.0) * 100.0) / 100.0;
    }
    
    /**
     * Determines if the image is in landscape orientation.
     * 
     * @return true if width > height
     */
    public boolean isLandscape() {
        return width != null && height != null && width > height;
    }
    
    /**
     * Determines if the image is in portrait orientation.
     * 
     * @return true if height > width
     */
    public boolean isPortrait() {
        return width != null && height != null && height > width;
    }
    
    /**
     * Determines if the image is square.
     * 
     * @return true if width equals height
     */
    public boolean isSquare() {
        return width != null && height != null && width.equals(height);
    }
}
