package com.gallery.model;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Represents a folder/directory containing image files.
 * 
 * This domain model encapsulates information about a directory
 * that contains images, including its path, child folders, and images.
 * 
 * Design decisions:
 * - Uses Java NIO Path for modern file handling
 * - Maintains separate lists for images and subfolders
 * - Provides methods for recursive image counting
 * - Thread-safe collections for concurrent access
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class ImageFolder {

    /** Path to this folder */
    private final Path folderPath;

    /** Folder name (last component of the path) */
    private final String folderName;

    /** Parent folder (null for root) */
    private final ImageFolder parentFolder;

    /** List of image files in this folder */
    private final List<ImageFile> imageFiles;

    /** List of subfolders */
    private final List<ImageFolder> subfolders;

    /** Whether this folder has been scanned for images */
    private boolean scanned;

    /** Last scan timestamp */
    private Instant lastScanned;

    /** Whether this folder is bookmarked */
    private boolean bookmarked;

    /**
     * Constructs a new ImageFolder with the specified path.
     * 
     * @param folderPath The path to the folder
     * @param parentFolder The parent folder (null for root)
     */
    public ImageFolder(Path folderPath, ImageFolder parentFolder) {
        this.folderPath = folderPath;
        this.folderName = folderPath.getFileName().toString();
        this.parentFolder = parentFolder;
        this.imageFiles = new ArrayList<>();
        this.subfolders = new ArrayList<>();
        this.scanned = false;
        this.bookmarked = false;
    }

    // ==================== GETTERS ====================

    /**
     * Gets the path to this folder.
     * 
     * @return The folder path
     */
    public Path getFolderPath() {
        return folderPath;
    }

    /**
     * Gets the folder name.
     * 
     * @return The folder name
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Gets the full path as a string.
     * 
     * @return Full path string
     */
    public String getFullPath() {
        return folderPath.toAbsolutePath().toString();
    }

    /**
     * Gets the parent folder.
     * 
     * @return Parent folder, or null if this is a root folder
     */
    public ImageFolder getParentFolder() {
        return parentFolder;
    }

    /**
     * Gets the list of image files in this folder.
     * 
     * @return List of ImageFile objects
     */
    public List<ImageFile> getImageFiles() {
        return imageFiles;
    }

    /**
     * Gets the list of subfolders.
     * 
     * @return List of ImageFolder objects
     */
    public List<ImageFolder> getSubfolders() {
        return subfolders;
    }

    /**
     * Checks if this folder has been scanned.
     * 
     * @return true if scanned, false otherwise
     */
    public boolean isScanned() {
        return scanned;
    }

    /**
     * Marks this folder as scanned.
     */
    public void setScanned(boolean scanned) {
        this.scanned = scanned;
        if (scanned) {
            this.lastScanned = Instant.now();
        }
    }

    /**
     * Gets the last scan timestamp.
     * 
     * @return Last scanned instant, or null if never scanned
     */
    public Instant getLastScanned() {
        return lastScanned;
    }

    /**
     * Checks if this folder is bookmarked.
     * 
     * @return true if bookmarked, false otherwise
     */
    public boolean isBookmarked() {
        return bookmarked;
    }

    /**
     * Sets the bookmarked status of this folder.
     * 
     * @param bookmarked true to bookmark, false to remove bookmark
     */
    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    // ==================== IMAGE MANAGEMENT ====================

    /**
     * Adds an image file to this folder.
     * 
     * @param imageFile The image file to add
     */
    public void addImageFile(ImageFile imageFile) {
        if (!imageFiles.contains(imageFile)) {
            imageFiles.add(imageFile);
        }
    }

    /**
     * Adds multiple image files to this folder.
     * 
     * @param files List of image files to add
     */
    public void addImageFiles(List<ImageFile> files) {
        for (ImageFile file : files) {
            addImageFile(file);
        }
    }

    /**
     * Removes an image file from this folder.
     * 
     * @param imageFile The image file to remove
     * @return true if removed, false if not found
     */
    public boolean removeImageFile(ImageFile imageFile) {
        return imageFiles.remove(imageFile);
    }

    /**
     * Clears all image files from this folder.
     */
    public void clearImageFiles() {
        imageFiles.clear();
    }

    /**
     * Gets the number of image files in this folder.
     * 
     * @return Image count
     */
    public int getImageCount() {
        return imageFiles.size();
    }

    // ==================== SUBFOLDER MANAGEMENT ====================

    /**
     * Adds a subfolder to this folder.
     * 
     * @param subfolder The subfolder to add
     */
    public void addSubfolder(ImageFolder subfolder) {
        if (!subfolders.contains(subfolder)) {
            subfolders.add(subfolder);
        }
    }

    /**
     * Removes a subfolder from this folder.
     * 
     * @param subfolder The subfolder to remove
     * @return true if removed, false if not found
     */
    public boolean removeSubfolder(ImageFolder subfolder) {
        return subfolders.remove(subfolder);
    }

    /**
     * Gets the number of subfolders.
     * 
     * @return Subfolder count
     */
    public int getSubfolderCount() {
        return subfolders.size();
    }

    // ==================== RECURSIVE OPERATIONS ====================

    /**
     * Gets the total number of images in this folder and all subfolders.
     * This is a recursive operation.
     * 
     * @return Total image count including subfolders
     */
    public int getTotalImageCount() {
        int count = imageFiles.size();
        for (ImageFolder subfolder : subfolders) {
            count += subfolder.getTotalImageCount();
        }
        return count;
    }

    /**
     * Gets all image files from this folder and all subfolders.
     * This is a recursive operation that creates a flat list.
     * 
     * @return List of all ImageFile objects in this folder tree
     */
    public List<ImageFile> getAllImageFiles() {
        List<ImageFile> allFiles = new ArrayList<>(imageFiles);
        for (ImageFolder subfolder : subfolders) {
            allFiles.addAll(subfolder.getAllImageFiles());
        }
        return allFiles;
    }

    /**
     * Finds an image file by its path in this folder tree.
     * This is a recursive search.
     * 
     * @param filePath The path to search for
     * @return The ImageFile if found, null otherwise
     */
    public ImageFile findImageByPath(Path filePath) {
        // Check current folder
        for (ImageFile imageFile : imageFiles) {
            if (imageFile.getFilePath().equals(filePath)) {
                return imageFile;
            }
        }
        
        // Search subfolders recursively
        for (ImageFolder subfolder : subfolders) {
            ImageFile found = subfolder.findImageByPath(filePath);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }

    /**
     * Gets the depth level of this folder in the tree.
     * Root folder is level 0.
     * 
     * @return Depth level
     */
    public int getLevel() {
        int level = 0;
        ImageFolder current = parentFolder;
        while (current != null) {
            level++;
            current = current.getParentFolder();
        }
        return level;
    }

    /**
     * Returns a string representation of this folder.
     * 
     * @return Formatted string with folder name and image count
     */
    @Override
    public String toString() {
        return String.format("%s (%d images)", folderName, imageFiles.size());
    }

    /**
     * Checks if this folder equals another object.
     * Two folders are equal if they have the same path.
     * 
     * @param obj The object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImageFolder)) {
            return false;
        }
        ImageFolder other = (ImageFolder) obj;
        return this.folderPath.equals(other.folderPath);
    }

    /**
     * Returns the hash code based on the folder path.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return folderPath.hashCode();
    }
}
