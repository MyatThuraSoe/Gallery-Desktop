package com.gallery.exception;

/**
 * Exception thrown when a file operation fails.
 * 
 * This is a checked exception that covers various file-related errors:
 * - File not found
 * - Permission denied
 * - Disk full
 * - Invalid path
 * - File locked by another process
 * 
 * Using specific exceptions allows for targeted error handling and
 * better user feedback.
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class FileOperationException extends GalleryException {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** The file path involved in the failed operation */
    private final String filePath;

    /** The type of operation that failed (e.g., "COPY", "MOVE", "DELETE") */
    private final String operationType;

    /**
     * Constructs a new FileOperationException with the specified detail message,
     * file path, and operation type.
     * 
     * @param message       The detail message explaining the exception
     * @param filePath      The path to the file involved in the operation
     * @param operationType The type of operation that failed
     */
    public FileOperationException(String message, String filePath, String operationType) {
        super(message, "FILE_OP_FAILED");
        this.filePath = filePath;
        this.operationType = operationType;
    }

    /**
     * Constructs a new FileOperationException with the specified detail message,
     * file path, operation type, and underlying cause.
     * 
     * @param message       The detail message explaining the exception
     * @param filePath      The path to the file involved in the operation
     * @param operationType The type of operation that failed
     * @param cause         The underlying cause of this exception
     */
    public FileOperationException(String message, String filePath, String operationType, Throwable cause) {
        super(message, cause, "FILE_OP_FAILED");
        this.filePath = filePath;
        this.operationType = operationType;
    }

    /**
     * Gets the file path involved in the failed operation.
     * 
     * @return The file path as a String
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Gets the type of operation that failed.
     * Common values: "COPY", "MOVE", "DELETE", "RENAME", "CREATE"
     * 
     * @return The operation type as a String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Returns a formatted string representation of this exception
     * including file path and operation type.
     * 
     * @return Formatted exception details
     */
    @Override
    public String toString() {
        return String.format(
            "FileOperationException [%s] at %d: %s (file: %s, operation: %s)",
            getErrorCode(),
            getTimestamp(),
            getMessage(),
            filePath,
            operationType
        );
    }
}
