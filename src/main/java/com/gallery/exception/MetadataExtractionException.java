package com.gallery.exception;

/**
 * Exception thrown when metadata extraction fails.
 * 
 * This exception covers failures in reading EXIF, IPTC, XMP, or other
 * metadata from image files. Common causes include:
 * - Corrupted metadata segments
 * - Unsupported metadata format
 * - File access issues during extraction
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class MetadataExtractionException extends GalleryException {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** The file path from which metadata extraction failed */
    private final String filePath;

    /**
     * Constructs a new MetadataExtractionException with the specified detail message
     * and file path.
     * 
     * @param message  The detail message explaining the exception
     * @param filePath The path to the file from which metadata extraction failed
     */
    public MetadataExtractionException(String message, String filePath) {
        super(message, "METADATA_EXTRACTION_FAILED");
        this.filePath = filePath;
    }

    /**
     * Constructs a new MetadataExtractionException with the specified detail message,
     * file path, and underlying cause.
     * 
     * @param message  The detail message explaining the exception
     * @param filePath The path to the file from which metadata extraction failed
     * @param cause    The underlying cause of this exception
     */
    public MetadataExtractionException(String message, String filePath, Throwable cause) {
        super(message, cause, "METADATA_EXTRACTION_FAILED");
        this.filePath = filePath;
    }

    /**
     * Gets the file path from which metadata extraction failed.
     * 
     * @return The file path as a String
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns a formatted string representation of this exception.
     * 
     * @return Formatted exception details
     */
    @Override
    public String toString() {
        return String.format(
            "MetadataExtractionException [%s] at %d: %s (file: %s)",
            getErrorCode(),
            getTimestamp(),
            getMessage(),
            filePath
        );
    }
}
