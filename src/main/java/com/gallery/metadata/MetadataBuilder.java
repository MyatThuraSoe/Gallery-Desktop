package com.gallery.metadata;

import java.time.LocalDateTime;

/**
 * Builder pattern implementation for creating Metadata instances.
 * 
 * This builder provides a fluent API for constructing Metadata objects
 * with optional fields, avoiding telescoping constructor problems.
 * 
 * @author Gallery Team
 * @version 1.0
 */
public class MetadataBuilder {
    
    // Camera Information
    private String cameraMake;
    private String cameraModel;
    private String lensModel;
    
    // Exposure Settings
    private Double iso;
    private Double exposureTime;
    private Double fNumber;
    private Double focalLength;
    
    // Image Properties
    private Integer width;
    private Integer height;
    private Integer bitDepth;
    private String colorProfile;
    private String orientation;
    
    // Date & Time
    private LocalDateTime dateTaken;
    private LocalDateTime dateModified;
    
    // GPS Information
    private Double latitude;
    private Double longitude;
    private Double altitude;
    
    // File Information
    private Long fileSize;
    private String mimeType;
    private String fileExtension;
    
    /**
     * Sets the camera manufacturer.
     * 
     * @param cameraMake the camera make
     * @return this builder for method chaining
     */
    public MetadataBuilder cameraMake(String cameraMake) {
        this.cameraMake = cameraMake;
        return this;
    }
    
    /**
     * Sets the camera model.
     * 
     * @param cameraModel the camera model
     * @return this builder for method chaining
     */
    public MetadataBuilder cameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
        return this;
    }
    
    /**
     * Sets the lens model.
     * 
     * @param lensModel the lens model
     * @return this builder for method chaining
     */
    public MetadataBuilder lensModel(String lensModel) {
        this.lensModel = lensModel;
        return this;
    }
    
    /**
     * Sets the ISO value.
     * 
     * @param iso the ISO sensitivity
     * @return this builder for method chaining
     */
    public MetadataBuilder iso(Double iso) {
        this.iso = iso;
        return this;
    }
    
    /**
     * Sets the exposure time in seconds.
     * 
     * @param exposureTime the exposure time
     * @return this builder for method chaining
     */
    public MetadataBuilder exposureTime(Double exposureTime) {
        this.exposureTime = exposureTime;
        return this;
    }
    
    /**
     * Sets the f-number (aperture).
     * 
     * @param fNumber the f-number
     * @return this builder for method chaining
     */
    public MetadataBuilder fNumber(Double fNumber) {
        this.fNumber = fNumber;
        return this;
    }
    
    /**
     * Sets the focal length in millimeters.
     * 
     * @param focalLength the focal length
     * @return this builder for method chaining
     */
    public MetadataBuilder focalLength(Double focalLength) {
        this.focalLength = focalLength;
        return this;
    }
    
    /**
     * Sets the image width in pixels.
     * 
     * @param width the image width
     * @return this builder for method chaining
     */
    public MetadataBuilder width(Integer width) {
        this.width = width;
        return this;
    }
    
    /**
     * Sets the image height in pixels.
     * 
     * @param height the image height
     * @return this builder for method chaining
     */
    public MetadataBuilder height(Integer height) {
        this.height = height;
        return this;
    }
    
    /**
     * Sets the bit depth (e.g., 8, 16, 24).
     * 
     * @param bitDepth the bit depth
     * @return this builder for method chaining
     */
    public MetadataBuilder bitDepth(Integer bitDepth) {
        this.bitDepth = bitDepth;
        return this;
    }
    
    /**
     * Sets the color profile name.
     * 
     * @param colorProfile the color profile (e.g., "sRGB", "Adobe RGB")
     * @return this builder for method chaining
     */
    public MetadataBuilder colorProfile(String colorProfile) {
        this.colorProfile = colorProfile;
        return this;
    }
    
    /**
     * Sets the orientation string.
     * 
     * @param orientation the orientation (e.g., "Horizontal", "Rotate 90")
     * @return this builder for method chaining
     */
    public MetadataBuilder orientation(String orientation) {
        this.orientation = orientation;
        return this;
    }
    
    /**
     * Sets the date when the photo was taken.
     * 
     * @param dateTaken the date taken
     * @return this builder for method chaining
     */
    public MetadataBuilder dateTaken(LocalDateTime dateTaken) {
        this.dateTaken = dateTaken;
        return this;
    }
    
    /**
     * Sets the date when the file was modified.
     * 
     * @param dateModified the modification date
     * @return this builder for method chaining
     */
    public MetadataBuilder dateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
        return this;
    }
    
    /**
     * Sets the GPS latitude.
     * 
     * @param latitude the latitude in decimal degrees
     * @return this builder for method chaining
     */
    public MetadataBuilder latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }
    
    /**
     * Sets the GPS longitude.
     * 
     * @param longitude the longitude in decimal degrees
     * @return this builder for method chaining
     */
    public MetadataBuilder longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }
    
    /**
     * Sets the GPS altitude.
     * 
     * @param altitude the altitude in meters
     * @return this builder for method chaining
     */
    public MetadataBuilder altitude(Double altitude) {
        this.altitude = altitude;
        return this;
    }
    
    /**
     * Sets the file size in bytes.
     * 
     * @param fileSize the file size
     * @return this builder for method chaining
     */
    public MetadataBuilder fileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }
    
    /**
     * Sets the MIME type.
     * 
     * @param mimeType the MIME type (e.g., "image/jpeg")
     * @return this builder for method chaining
     */
    public MetadataBuilder mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }
    
    /**
     * Sets the file extension.
     * 
     * @param fileExtension the file extension without dot (e.g., "jpg")
     * @return this builder for method chaining
     */
    public MetadataBuilder fileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }
    
    /**
     * Builds and returns the immutable Metadata instance.
     * 
     * @return a new Metadata object with all set values
     */
    public Metadata build() {
        return new Metadata(
            cameraMake, cameraModel, lensModel,
            iso, exposureTime, fNumber, focalLength,
            width, height, bitDepth, colorProfile, orientation,
            dateTaken, dateModified,
            latitude, longitude, altitude,
            fileSize, mimeType, fileExtension
        );
    }
}
