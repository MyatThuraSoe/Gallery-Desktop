package com.gallery.app;

import com.gallery.config.AppConfig;
import com.gallery.constants.AppConstants;
import com.gallery.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application entry point for the Gallery Desktop Application.
 * 
 * This class extends JavaFX Application and serves as the bootstrap class
 * that initializes and launches the application.
 * 
 * Architecture decisions:
 * - Minimal logic in this class (Single Responsibility Principle)
 * - Delegates initialization to AppConfig and services
 * - Sets up the main stage and scene
 * - Handles application lifecycle events
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class GalleryApplication extends Application {

    /** Logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(GalleryApplication.class);

    /** Application configuration */
    private AppConfig appConfig;

    /** Main view instance */
    private MainView mainView;

    /**
     * Entry point for JavaFX application launch.
     * This method is called by the JavaFX launcher.
     * 
     * @param args Command line arguments (not used currently)
     */
    public static void main(String[] args) {
        LOGGER.info("Starting {} version {}", AppConstants.APP_NAME, AppConstants.APP_VERSION);
        launch(args);
    }

    /**
     * Called when the application starts.
     * Initializes configuration, creates the main view, and sets up the stage.
     * 
     * @param primaryStage The primary stage for this application
     * @throws Exception If initialization fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.info("Initializing application...");
        
        try {
            // Initialize application configuration
            initConfig();
            
            // Create the main view
            mainView = new MainView(appConfig);
            
            // Create the scene with the main view
            Scene scene = new Scene(mainView.getRoot());
            
            // Configure the primary stage
            configureStage(primaryStage, scene);
            
            // Show the application window
            primaryStage.show();
            
            LOGGER.info("Application started successfully");
            
        } catch (Exception e) {
            LOGGER.error("Failed to start application", e);
            throw e;
        }
    }

    /**
     * Initializes the application configuration.
     * Loads settings from preferences and initializes services.
     */
    private void initConfig() {
        LOGGER.debug("Initializing configuration...");
        appConfig = new AppConfig();
        appConfig.load();
        LOGGER.debug("Configuration loaded successfully");
    }

    /**
     * Configures the primary stage with title, size, and other properties.
     * 
     * @param stage The stage to configure
     * @param scene The scene to display
     */
    private void configureStage(Stage stage, Scene scene) {
        // Set window title
        stage.setTitle(AppConstants.APP_NAME + " v" + AppConstants.APP_VERSION);
        
        // Set minimum window size
        stage.setMinWidth(AppConstants.MIN_WINDOW_WIDTH);
        stage.setMinHeight(AppConstants.MIN_WINDOW_HEIGHT);
        
        // Apply saved window size if available
        applySavedWindowSize(stage);
        
        // Set the scene
        stage.setScene(scene);
        
        // Handle window close request
        stage.setOnCloseRequest((WindowEvent event) -> {
            LOGGER.info("Application closing...");
            onSaveWindowState(stage);
            onShutdown();
        });
    }

    /**
     * Applies saved window size from preferences.
     * 
     * @param stage The stage to resize
     */
    private void applySavedWindowSize(Stage stage) {
        double width = appConfig.getDouble(AppConstants.PREFS_WINDOW_WIDTH, 
                                           AppConstants.DEFAULT_WINDOW_WIDTH);
        double height = appConfig.getDouble(AppConstants.PREFS_WINDOW_HEIGHT, 
                                            AppConstants.DEFAULT_WINDOW_HEIGHT);
        
        stage.setWidth(width);
        stage.setHeight(height);
        
        LOGGER.debug("Window size set to {}x{}", width, height);
    }

    /**
     * Saves the current window state to preferences.
     * Called when the application is closing.
     * 
     * @param stage The stage to save state from
     */
    private void onSaveWindowState(Stage stage) {
        appConfig.setDouble(AppConstants.PREFS_WINDOW_WIDTH, stage.getWidth());
        appConfig.setDouble(AppConstants.PREFS_WINDOW_HEIGHT, stage.getHeight());
        appConfig.save();
        
        LOGGER.debug("Window state saved: {}x{}", stage.getWidth(), stage.getHeight());
    }

    /**
     * Called when the application shuts down.
     * Cleans up resources and saves final state.
     */
    private void onShutdown() {
        LOGGER.info("Performing shutdown cleanup...");
        
        // Save configuration
        appConfig.save();
        
        // Clear caches
        // (Cache manager will be implemented later)
        
        // Shutdown executor services
        // (Thread pool management will be implemented later)
        
        LOGGER.info("Shutdown complete");
    }

    /**
     * Called when the application stops.
     * JavaFX lifecycle method for cleanup.
     * 
     * @throws Exception If shutdown fails
     */
    @Override
    public void stop() throws Exception {
        LOGGER.info("Application stopped");
        super.stop();
    }
}
