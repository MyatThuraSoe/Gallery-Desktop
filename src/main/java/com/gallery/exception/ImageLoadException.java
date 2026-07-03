package com.gallery.exception;

/**
 * Exception thrown when an image file cannot be loaded or found.
 * 
 * This is a checked exception that must be handled by the caller.
 * It provides specific information about image loading failures,
 * including the file path and the underlying cause.
 * 
 * Usage example:
 * <pre>
 * try {
 *     ImageLoader.load(imagePath);
 * } catch (ImageLoadException e) {
 *     logger.error("Failed to load image: " + e.getFilePath(), e);
 *     showErrorToUser("Cannot load image: " + e.getMessage());
 * }
 * </pre>
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class ImageLoadException extends GalleryException {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** The file path of the image that failed to load */
    private final String filePath;

    /**
     * Constructs a new ImageLoadException with the specified detail message
     * and file path.
     * 
     * @param message  The detail message explaining the exception
     * @param filePath The path to the image file that failed to load
     */
    public ImageLoadException(String message, String filePath) {
        super(message, "IMG_LOAD_FAILED");
        this.filePath = filePath;
    }

    /**
     * Constructs a new ImageLoadException with the specified detail message,
     * file path, and underlying cause.
     * 
     * @param message  The detail message explaining the exception
     * @param filePath The path to the image file that failed to load
     * @param cause    The underlying cause of this exception
     */
    public ImageLoadException(String message, String filePath, Throwable cause) {
        super(message, cause, "IMG_LOAD_FAILED");
        this.filePath = filePath;
    }

    /**
     * Gets the file path of the image that failed to load.
     * This can be used for logging, error reporting, or retry logic.
     * 
     * @return The file path as a String
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns a formatted string representation of this exception
     * including the file path.
     * 
     * @return Formatted exception details with file path
     */
    @Override
    public String toString() {
        return String.format(
            "ImageLoadException [%s] at %d: %s (file: %s)",
            getErrorCode(),
            getTimestamp(),
            getMessage(),
            filePath
        );
    }
}
