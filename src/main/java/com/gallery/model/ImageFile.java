package com.gallery.model;

import java.nio.file.Path;
import java.time.Instant;

/**
 * Represents an image file in the gallery application.
 * 
 * This is a core domain model that encapsulates all information about
 * an image file, including its path, dimensions, metadata, and UI state.
 * 
 * Design decisions:
 * - Immutable fields with getters only (thread-safe)
 * - Lazy loading of metadata to improve performance
 * - Separate UI state (selected, favorite) from file data
 * - Uses Java NIO Path for modern file handling
 * - Uses java.time for date/time handling
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class ImageFile {

    /** Unique identifier for this image */
    private final String id;

    /** Path to the image file */
    private final Path filePath;

    /** Filename without extension */
    private final String fileName;

    /** File extension (lowercase, without dot) */
    private final String extension;

    /** File size in bytes */
    private final long fileSize;

    /** Last modified timestamp */
    private final Instant lastModified;

    /** Image width in pixels (loaded lazily) */
    private int width;

    /** Image height in pixels (loaded lazily) */
    private int height;

    /** Whether the image dimensions have been loaded */
    private boolean dimensionsLoaded;

    /** Whether this image is marked as favorite */
    private boolean favorite;

    /** Whether this image is currently selected in the UI */
    private boolean selected;

    /**
     * Constructs a new ImageFile with the specified file path.
     * 
     * @param filePath   The path to the image file
     * @param fileName   The filename without extension
     * @param extension  The file extension (lowercase)
     * @param fileSize   The file size in bytes
     * @param lastModified The last modified timestamp
     */
    public ImageFile(Path filePath, String fileName, String extension, 
                     long fileSize, Instant lastModified) {
        this.id = generateId(filePath);
        this.filePath = filePath;
        this.fileName = fileName;
        this.extension = extension.toLowerCase();
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.dimensionsLoaded = false;
        this.favorite = false;
        this.selected = false;
    }

    /**
     * Generates a unique ID based on the file path.
     * Using absolute path string ensures uniqueness across the application.
     * 
     * @param filePath The file path
     * @return Unique identifier string
     */
    private String generateId(Path filePath) {
        return filePath.toAbsolutePath().toString();
    }

    // ==================== GETTERS ====================

    /**
     * Gets the unique identifier for this image.
     * 
     * @return The ID string
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the path to the image file.
     * 
     * @return The file path
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Gets the path to the image file.
     * 
     * @return The file path
     */
    public Path getPath() {
        return filePath;
    }

    /**
     * Gets the filename without extension.
     * 
     * @return The filename
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the file extension (lowercase, without dot).
     * 
     * @return The extension (e.g., "jpg", "png")
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Gets the file size in bytes.
     * 
     * @return File size in bytes
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Gets the last modified timestamp.
     * 
     * @return Instant representing last modified time
     */
    public Instant getLastModified() {
        return lastModified;
    }

    /**
     * Gets the image width in pixels.
     * May return 0 if dimensions haven't been loaded yet.
     * 
     * @return Width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the image width (called after loading dimensions).
     * 
     * @param width The width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the image height in pixels.
     * May return 0 if dimensions haven't been loaded yet.
     * 
     * @return Height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the image height (called after loading dimensions).
     * 
     * @param height The height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Checks if the image dimensions have been loaded.
     * 
     * @return true if dimensions are loaded, false otherwise
     */
    public boolean isDimensionsLoaded() {
        return dimensionsLoaded;
    }

    /**
     * Marks the image dimensions as loaded.
     */
    public void setDimensionsLoaded() {
        this.dimensionsLoaded = true;
    }

    /**
     * Gets the resolution in megapixels.
     * 
     * @return Resolution in MP (width * height / 1,000,000)
     */
    public double getResolution() {
        if (!dimensionsLoaded) {
            return 0.0;
        }
        return (double) width * height / 1_000_000.0;
    }

    /**
     * Checks if the image has landscape orientation.
     * 
     * @return true if width > height, false otherwise
     */
    public boolean isLandscape() {
        return dimensionsLoaded && width > height;
    }

    /**
     * Checks if the image has portrait orientation.
     * 
     * @return true if height > width, false otherwise
     */
    public boolean isPortrait() {
        return dimensionsLoaded && height > width;
    }

    /**
     * Checks if the image is square.
     * 
     * @return true if width == height, false otherwise
     */
    public boolean isSquare() {
        return dimensionsLoaded && width == height;
    }

    /**
     * Checks if this image is marked as favorite.
     * 
     * @return true if favorite, false otherwise
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Sets the favorite status of this image.
     * 
     * @param favorite true to mark as favorite, false otherwise
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * Checks if this image is currently selected in the UI.
     * 
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the selected status of this image.
     * 
     * @param selected true to mark as selected, false otherwise
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Gets the full filename with extension.
     * 
     * @return Filename with extension (e.g., "photo.jpg")
     */
    public String getFullName() {
        return fileName + "." + extension;
    }

    /**
     * Gets the file size as a human-readable string.
     * 
     * @return Formatted file size (e.g., "2.5 MB")
     */
    public String getFileSizeFormatted() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Returns a string representation of this image file.
     * 
     * @return Formatted string with filename and dimensions
     */
    @Override
    public String toString() {
        if (dimensionsLoaded) {
            return String.format("%s (%dx%d)", getFullName(), width, height);
        }
        return getFullName();
    }

    /**
     * Checks if this image file equals another object.
     * Two image files are equal if they have the same file path.
     * 
     * @param obj The object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImageFile)) {
            return false;
        }
        ImageFile other = (ImageFile) obj;
        return this.filePath.equals(other.filePath);
    }

    /**
     * Returns the hash code based on the file path.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}
