package com.gallery.config;

import com.gallery.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

/**
 * Application configuration manager.
 * 
 * This class handles loading and saving application settings using
 * Java Preferences API. It provides a centralized way to manage
 * user preferences and application state.
 * 
 * Design decisions:
 * - Uses Java Preferences API for cross-platform storage
 * - Provides type-safe getters and setters
 * - Implements lazy loading of preferences
 * - Thread-safe operations
 * - Default values for all settings
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class AppConfig {

    /** Logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    /** Preferences node for this application */
    private final Preferences prefs;

    /** Whether configuration has been loaded */
    private boolean loaded;

    /**
     * Constructs a new AppConfig instance.
     * Initializes the preferences node but does not load data yet.
     */
    public AppConfig() {
        this.prefs = Preferences.userNodeForPackage(AppConfig.class);
        this.loaded = false;
        LOGGER.debug("AppConfig initialized with node: {}", AppConstants.PREFS_NODE_PATH);
    }

    /**
     * Loads configuration from preferences.
     * This is called once during application startup.
     */
    public void load() {
        if (loaded) {
            LOGGER.debug("Configuration already loaded, skipping");
            return;
        }

        LOGGER.info("Loading configuration from preferences...");
        
        // Force loading by accessing a preference
        prefs.node(AppConstants.PREFS_NODE_PATH);
        
        loaded = true;
        LOGGER.info("Configuration loaded successfully");
    }

    /**
     * Saves configuration to preferences.
     * Should be called when settings change or on shutdown.
     */
    public void save() {
        LOGGER.debug("Saving configuration to preferences...");
        
        try {
            prefs.flush();
            LOGGER.info("Configuration saved successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to save configuration", e);
        }
    }

    // ==================== STRING PREFERENCES ====================

    /**
     * Gets a string preference value.
     * 
     * @param key The preference key
     * @param defaultValue The default value if not found
     * @return The preference value or default
     */
    public String getString(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }

    /**
     * Sets a string preference value.
     * 
     * @param key The preference key
     * @param value The value to set
     */
    public void setString(String key, String value) {
        prefs.put(key, value);
        LOGGER.debug("Set string preference: {} = {}", key, value);
    }

    // ==================== INTEGER PREFERENCES ====================

    /**
     * Gets an integer preference value.
     * 
     * @param key The preference key
     * @param defaultValue The default value if not found
     * @return The preference value or default
     */
    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    /**
     * Sets an integer preference value.
     * 
     * @param key The preference key
     * @param value The value to set
     */
    public void setInt(String key, int value) {
        prefs.putInt(key, value);
        LOGGER.debug("Set int preference: {} = {}", key, value);
    }

    // ==================== DOUBLE PREFERENCES ====================

    /**
     * Gets a double preference value.
     * 
     * @param key The preference key
     * @param defaultValue The default value if not found
     * @return The preference value or default
     */
    public double getDouble(String key, double defaultValue) {
        return prefs.getDouble(key, defaultValue);
    }

    /**
     * Sets a double preference value.
     * 
     * @param key The preference key
     * @param value The value to set
     */
    public void setDouble(String key, double value) {
        prefs.putDouble(key, value);
        LOGGER.debug("Set double preference: {} = {}", key, value);
    }

    // ==================== BOOLEAN PREFERENCES ====================

    /**
     * Gets a boolean preference value.
     * 
     * @param key The preference key
     * @param defaultValue The default value if not found
     * @return The preference value or default
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * Sets a boolean preference value.
     * 
     * @param key The preference key
     * @param value The value to set
     */
    public void setBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
        LOGGER.debug("Set boolean preference: {} = {}", key, value);
    }

    // ==================== LONG PREFERENCES ====================

    /**
     * Gets a long preference value.
     * 
     * @param key The preference key
     * @param defaultValue The default value if not found
     * @return The preference value or default
     */
    public long getLong(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }

    /**
     * Sets a long preference value.
     * 
     * @param key The preference key
     * @param value The value to set
     */
    public void setLong(String key, long value) {
        prefs.putLong(key, value);
        LOGGER.debug("Set long preference: {} = {}", key, value);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if a preference key exists.
     * 
     * @param key The preference key to check
     * @return true if exists, false otherwise
     */
    public boolean containsKey(String key) {
        try {
            return prefs.keys().length > 0 && 
                   java.util.Arrays.asList(prefs.keys()).contains(key);
        } catch (Exception e) {
            LOGGER.warn("Error checking preference key: {}", key, e);
            return false;
        }
    }

    /**
     * Removes a preference key.
     * 
     * @param key The preference key to remove
     */
    public void remove(String key) {
        prefs.remove(key);
        LOGGER.debug("Removed preference: {}", key);
    }

    /**
     * Clears all preferences.
     * Use with caution!
     */
    public void clear() {
        try {
            prefs.clear();
            LOGGER.info("All preferences cleared");
        } catch (Exception e) {
            LOGGER.error("Failed to clear preferences", e);
        }
    }

    /**
     * Gets the underlying Preferences object.
     * For advanced operations not covered by this class.
     * 
     * @return The Preferences instance
     */
    public Preferences getPreferences() {
        return prefs;
    }

    /**
     * Checks if configuration has been loaded.
     * 
     * @return true if loaded, false otherwise
     */
    public boolean isLoaded() {
        return loaded;
    }

    // ==================== WINDOW SIZE PREFERENCES ====================

    /**
     * Gets the saved window width.
     * 
     * @return The window width in pixels
     */
    public double getWindowWidth() {
        return getDouble("window.width", 1200.0);
    }

    /**
     * Sets the window width.
     * 
     * @param width The width in pixels
     */
    public void setWindowWidth(double width) {
        setDouble("window.width", width);
    }

    /**
     * Gets the saved window height.
     * 
     * @return The window height in pixels
     */
    public double getWindowHeight() {
        return getDouble("window.height", 800.0);
    }

    /**
     * Sets the window height.
     * 
     * @param height The height in pixels
     */
    public void setWindowHeight(double height) {
        setDouble("window.height", height);
    }

    // ==================== GRID LAYOUT PREFERENCES ====================

    /**
     * Gets the saved grid layout mode.
     * 
     * @return The grid layout mode (e.g., "Auto", "4 Columns")
     */
    public String getGridLayout() {
        return getString("grid.layout", "Auto");
    }

    /**
     * Sets the grid layout mode.
     * 
     * @param layout The layout mode
     */
    public void setGridLayout(String layout) {
        setString("grid.layout", layout);
    }

    /**
     * Gets the saved thumbnail size.
     * 
     * @return The thumbnail size in pixels
     */
    public double getThumbnailSize() {
        return getDouble("thumbnail.size", 150.0);
    }

    /**
     * Sets the thumbnail size.
     * 
     * @param size The size in pixels
     */
    public void setThumbnailSize(double size) {
        setDouble("thumbnail.size", size);
    }

    /**
     * Gets the slideshow interval in seconds.
     * 
     * @return The interval in seconds
     */
    public int getSlideshowInterval() {
        return getInt(AppConstants.PREFS_SLIDESHOW_INTERVAL, AppConstants.DEFAULT_SLIDESHOW_INTERVAL_SEC);
    }

    /**
     * Sets the slideshow interval in seconds.
     * 
     * @param seconds The interval in seconds
     */
    public void setSlideshowInterval(int seconds) {
        setInt(AppConstants.PREFS_SLIDESHOW_INTERVAL, Math.max(1, seconds));
    }
}
