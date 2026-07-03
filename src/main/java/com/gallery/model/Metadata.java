package com.gallery.model;

import java.time.LocalDateTime;

/**
 * Metadata - Immutable record for storing image metadata information.
 * 
 * WHY THIS DESIGN:
 * - Uses Java 21 record for immutability and concise syntax
 * - Contains all EXIF and file metadata fields
 * - Provides default values for optional fields
 * - Easy to pass around services and controllers
 */
public record Metadata(
    String filename,
    String filePath,
    long fileSize,
    String mimeType,
    int width,
    int height,
    String cameraMake,
    String cameraModel,
    String lensModel,
    Integer isoSpeed,
    Double aperture,
    Double shutterSpeed,
    Double focalLength,
    LocalDateTime dateTaken,
    String colorProfile,
    Integer bitDepth,
    Double latitude,
    Double longitude,
    String orientation
) {
    
    /**
     * Create a minimal metadata with just basic info.
     */
    public static Metadata basic(String filename, String path, long size, int width, int height) {
        return new Metadata(
            filename, path, size, null, width, height,
            null, null, null, null, null, null, null,
            null, null, null, null, null, null
        );
    }
    
    /**
     * Check if GPS coordinates are available.
     */
    public boolean hasGPS() {
        return latitude != null && longitude != null;
    }
    
    /**
     * Get formatted GPS coordinates as string.
     */
    public String getFormattedGPS() {
        if (!hasGPS()) return "N/A";
        return String.format("%.6f, %.6f", latitude, longitude);
    }
    
    /**
     * Get resolution as formatted string.
     */
    public String getResolutionString() {
        return width + " × " + height;
    }
    
    /**
     * Calculate megapixels.
     */
    public double getMegapixels() {
        return (width * height) / 1_000_000.0;
    }
}
