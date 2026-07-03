package com.gallery.service;

import com.gallery.model.ImageFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for scanning folders and discovering image files.
 * 
 * WHY: Folder scanning can be very slow for large directories (100,000+ images).
 * This service performs recursive scanning on background threads with progress tracking,
 * filtering only supported image formats to build the gallery index.
 */
public class FolderScannerService {
    
    private static final Logger LOG = LoggerFactory.getLogger(FolderScannerService.class);
    
    /**
     * Supported image extensions (lowercase, without dot).
     */
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif", "svg", "ico"
    );
    
    /**
     * Executor service for background folder scanning.
     */
    private final ExecutorService executor;
    
    /**
     * Creates a FolderScannerService with default settings.
     */
    public FolderScannerService() {
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("Folder-Scanner");
            t.setDaemon(true);
            return t;
        });
        LOG.info("FolderScannerService initialized");
    }
    
    /**
     * Scans a folder recursively for image files asynchronously.
     * 
     * @param folderPath The folder to scan
     * @return CompletableFuture containing list of discovered ImageFiles
     */
    public CompletableFuture<List<ImageFile>> scanFolderAsync(Path folderPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return scanFolder(folderPath);
            } catch (IOException e) {
                LOG.error("Failed to scan folder: {}", folderPath, e);
                return Collections.emptyList();
            }
        }, executor);
    }
    
    /**
     * Scans a folder recursively for image files synchronously.
     * Should only be called from background threads.
     * 
     * @param folderPath The folder to scan
     * @return List of discovered ImageFiles
     * @throws IOException If scanning fails
     */
    public List<ImageFile> scanFolder(Path folderPath) throws IOException {
        LOG.info("Starting folder scan: {}", folderPath);
        
        if (!Files.exists(folderPath)) {
            throw new IOException("Folder does not exist: " + folderPath);
        }
        
        if (!Files.isDirectory(folderPath)) {
            throw new IOException("Path is not a directory: " + folderPath);
        }
        
        List<ImageFile> imageFiles = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        
        Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String fileName = file.getFileName().toString().toLowerCase();
                int lastDotIndex = fileName.lastIndexOf('.');
                
                if (lastDotIndex > 0) {
                    String extension = fileName.substring(lastDotIndex + 1);
                    
                    if (SUPPORTED_EXTENSIONS.contains(extension)) {
                        try {
                            String nameWithoutExt = fileName.substring(0, lastDotIndex);
                            ImageFile imageFile = new ImageFile(
                                file,
                                nameWithoutExt,
                                extension,
                                attrs.size(),
                                attrs.lastModifiedTime().toInstant()
                            );
                            
                            imageFiles.add(imageFile);
                            int currentCount = count.incrementAndGet();
                            
                            if (currentCount % 1000 == 0) {
                                LOG.debug("Scanned {} images...", currentCount);
                            }
                            
                        } catch (Exception e) {
                            LOG.warn("Failed to process file: {}", file, e);
                        }
                    }
                }
                
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // Skip hidden directories
                if (dir.getFileName().toString().startsWith(".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                LOG.warn("Failed to access file: {}", file, exc);
                return FileVisitResult.CONTINUE;
            }
        });
        
        long elapsed = System.currentTimeMillis() - startTime;
        LOG.info("Folder scan completed: {} images found in {} ms", imageFiles.size(), elapsed);
        
        return imageFiles;
    }
    
    /**
     * Scans multiple folders and combines results.
     * 
     * @param folderPaths List of folders to scan
     * @return CompletableFuture containing combined list of ImageFiles
     */
    public CompletableFuture<List<ImageFile>> scanFoldersAsync(List<Path> folderPaths) {
        return CompletableFuture.supplyAsync(() -> {
            List<ImageFile> allFiles = new ArrayList<>();
            
            for (Path folderPath : folderPaths) {
                try {
                    List<ImageFile> files = scanFolder(folderPath);
                    allFiles.addAll(files);
                    LOG.info("Scanned {}: {} images", folderPath, files.size());
                } catch (IOException e) {
                    LOG.error("Failed to scan folder: {}", folderPath, e);
                }
            }
            
            return allFiles;
        }, executor);
    }
    
    /**
     * Checks if a file has a supported image extension.
     * 
     * @param path Path to check
     * @return true if supported image format
     */
    public boolean isSupportedImage(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0) {
            String extension = fileName.substring(lastDotIndex + 1);
            return SUPPORTED_EXTENSIONS.contains(extension);
        }
        
        return false;
    }
    
    /**
     * Gets the set of supported image extensions.
     * 
     * @return Unmodifiable set of extensions
     */
    public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(SUPPORTED_EXTENSIONS);
    }
    
    /**
     * Shuts down the executor service.
     */
    public void shutdown() {
        executor.shutdown();
        LOG.info("FolderScannerService shut down");
    }
}
