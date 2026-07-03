package com.gallery.service;

import com.gallery.config.AppConfig;
import com.gallery.model.ImageFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Manages the slideshow playback state, timing, and navigation.
 * Uses a ScheduledExecutorService for non-blocking timing.
 * 
 * WHY THIS DESIGN:
 * - Separation of concerns: Slideshow logic is separate from UI controller
 * - Non-blocking: Uses ScheduledExecutorService instead of Timeline to avoid FX thread issues
 * - Callback-based: Decoupled from specific UI components via Consumer callbacks
 * - Configurable: Integrates with AppConfig for persistent interval settings
 */
public class SlideshowService {
    private static final Logger logger = LoggerFactory.getLogger(SlideshowService.class);

    private final ScheduledExecutorService scheduler;
    private final AppConfig config;
    
    private boolean isPlaying;
    private int currentIntervalSeconds;
    private List<ImageFile> images;
    private int currentIndex;
    
    // Callbacks for decoupled communication
    private Consumer<Integer> onNextImageRequest;
    private Consumer<Boolean> onStateChange;

    public SlideshowService(AppConfig config) {
        this.config = config;
        // Single-thread scheduler dedicated to slideshow timing
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Slideshow-Scheduler");
            t.setDaemon(true);
            return t;
        });
        this.isPlaying = false;
        this.currentIntervalSeconds = config.getSlideshowInterval();
        this.currentIndex = 0;
    }

    /**
     * Initialize the slideshow with a list of images.
     */
    public void loadImages(List<ImageFile> images, int startIndex) {
        stop();
        this.images = images;
        this.currentIndex = startIndex;
        logger.info("Slideshow loaded {} images, starting at index {}", images.size(), startIndex);
    }

    /**
     * Start or Resume playback.
     */
    public void play() {
        if (isPlaying || images == null || images.isEmpty()) {
            return;
        }

        isPlaying = true;
        notifyStateChange();
        
        scheduleNext();
        logger.info("Slideshow started with interval {}s", currentIntervalSeconds);
    }

    /**
     * Pause playback.
     */
    public void pause() {
        if (!isPlaying) return;
        
        isPlaying = false;
        scheduler.purge(); // Remove cancelled tasks to free memory
        notifyStateChange();
        logger.info("Slideshow paused");
    }

    /**
     * Stop and reset state.
     */
    public void stop() {
        pause();
        currentIndex = 0;
    }

    /**
     * Toggle between Play and Pause.
     */
    public void toggle() {
        if (isPlaying) {
            pause();
        } else {
            play();
        }
    }

    /**
     * Set the interval in seconds. Restarts timer if currently playing.
     */
    public void setInterval(int seconds) {
        this.currentIntervalSeconds = seconds;
        config.setSlideshowInterval(seconds);
        if (isPlaying) {
            pause();
            play();
        }
    }

    /**
     * Manually trigger next image with wrap-around.
     */
    public void next() {
        if (images == null || images.isEmpty()) return;
        
        currentIndex = (currentIndex + 1) % images.size();
        notifyNextImage();
        
        // If playing, reset the timer so we don't jump immediately again
        if (isPlaying) {
            scheduler.purge();
            scheduleNext();
        }
    }

    /**
     * Manually trigger previous image with wrap-around.
     */
    public void previous() {
        if (images == null || images.isEmpty()) return;
        
        currentIndex = (currentIndex - 1 + images.size()) % images.size();
        notifyNextImage();

        if (isPlaying) {
            scheduler.purge();
            scheduleNext();
        }
    }

    /**
     * Schedule the next image transition.
     */
    private void scheduleNext() {
        scheduler.schedule(() -> {
            if (isPlaying) {
                next();
            }
        }, currentIntervalSeconds, TimeUnit.SECONDS);
    }

    private void notifyNextImage() {
        if (onNextImageRequest != null) {
            onNextImageRequest.accept(currentIndex);
        }
    }

    private void notifyStateChange() {
        if (onStateChange != null) {
            onStateChange.accept(isPlaying);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getCurrentInterval() {
        return currentIntervalSeconds;
    }

    public void setOnNextImageRequest(Consumer<Integer> callback) {
        this.onNextImageRequest = callback;
    }

    public void setOnStateChange(Consumer<Boolean> callback) {
        this.onStateChange = callback;
    }

    /**
     * Cleanup resources on application shutdown.
     */
    public void shutdown() {
        stop();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
