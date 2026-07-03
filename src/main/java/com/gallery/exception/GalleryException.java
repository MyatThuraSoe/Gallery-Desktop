package com.gallery.exception;

/**
 * Base exception class for all gallery application exceptions.
 * 
 * This abstract class serves as the parent for all custom exceptions
 * in the gallery application. Using a hierarchy of exceptions provides:
 * - Better error categorization
 * - Easier exception handling with catch blocks
 * - Clear separation between checked and unchecked exceptions
 * - Consistent error reporting across the application
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public abstract class GalleryException extends Exception {

    /** Unique error code for this exception type */
    private final String errorCode;
    
    /** Timestamp when the exception occurred */
    private final long timestamp;

    /**
     * Constructs a new GalleryException with the specified detail message.
     * 
     * @param message   The detail message explaining the exception
     * @param errorCode Unique error code for identification
     */
    protected GalleryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructs a new GalleryException with the specified detail message and cause.
     * 
     * @param message   The detail message explaining the exception
     * @param cause     The underlying cause of this exception
     * @param errorCode Unique error code for identification
     */
    protected GalleryException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Gets the error code associated with this exception.
     * Error codes follow the pattern: MODULE_ERROR_TYPE (e.g., IMG_NOT_FOUND)
     * 
     * @return The error code string
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the timestamp when this exception occurred.
     * Useful for logging and debugging purposes.
     * 
     * @return Unix timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a formatted string representation of this exception
     * including error code and timestamp.
     * 
     * @return Formatted exception details
     */
    @Override
    public String toString() {
        return String.format(
            "GalleryException [%s] at %d: %s",
            errorCode,
            timestamp,
            getMessage()
        );
    }
}
