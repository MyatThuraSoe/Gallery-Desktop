package com.gallery.view;

import com.gallery.config.AppConfig;
import com.gallery.constants.AppConstants;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application view container.
 * 
 * This class serves as the root container for all UI components in the
 * application. It uses a BorderPane layout to organize:
 * - Top: Toolbar
 * - Left: Sidebar (folder browser)
 * - Center: Main content (grid view / image viewer)
 * - Bottom: Status bar
 * 
 * Design decisions:
 * - Uses BorderPane for flexible region-based layout
 * - Separates view structure from controller logic (MVC)
 * - Provides methods to access sub-components
 * - Allows dynamic swapping of center content
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public class MainView extends BorderPane {

    /** Logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainView.class);

    /** Application configuration */
    private final AppConfig appConfig;

    /** Toolbar component */
    private HBox toolbar;

    /** Sidebar component (folder browser) */
    private VBox sidebar;

    /** Grid view component */
    private VirtualizedGridView gridView;

    /** Image viewer component */
    private ImageViewerComponent imageViewer;

    /** Status bar component */
    private HBox statusBar;

    /**
     * Constructs a new MainView with the specified configuration.
     * 
     * @param appConfig The application configuration
     */
    public MainView(AppConfig appConfig) {
        this.appConfig = appConfig;
        
        LOGGER.debug("Creating main view...");
        
        // Initialize the layout
        initializeLayout();
        
        LOGGER.debug("Main view created successfully");
    }

    /**
     * Initializes the main layout structure.
     * Sets up the BorderPane regions with actual components.
     */
    private void initializeLayout() {
        LOGGER.debug("Initializing layout...");
        
        // Apply default styling
        getStyleClass().add("main-view");
        
        // Set minimum size
        setMinWidth(AppConstants.MIN_WINDOW_WIDTH);
        setMinHeight(AppConstants.MIN_WINDOW_HEIGHT);
        
        // Create and set up regions
        createTopRegion();
        createLeftRegion();
        createCenterRegion();
        createBottomRegion();
        createRightRegion();
        
        LOGGER.debug("Layout initialization complete");
    }

    /**
     * Creates the top region (toolbar area).
     * Contains file operations, view controls, and search.
     */
    private void createTopRegion() {
        toolbar = new HBox(10);
        toolbar.getStyleClass().add("toolbar");
        toolbar.setPadding(new javafx.geometry.Insets(8, 16, 8, 16));
        
        // Add toolbar buttons (placeholders for now)
        Label titleLabel = new Label("📷 Professional Gallery");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label("Ready");
        statusLabel.setId("toolbar-status");
        
        toolbar.getChildren().addAll(titleLabel, spacer, statusLabel);
        setTop(toolbar);
        LOGGER.debug("Top region (toolbar) created");
    }

    /**
     * Creates the left region (sidebar).
     * Contains folder tree, bookmarks, and recent folders.
     */
    private void createLeftRegion() {
        sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new javafx.geometry.Insets(16));
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(200);
        
        Label sidebarTitle = new Label("📁 Folders");
        sidebarTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label placeholder = new Label("No folder selected\n\nClick 'Open Folder' to browse");
        placeholder.setStyle("-fx-text-fill: gray; -fx-alignment: center;");
        placeholder.setWrapText(true);
        
        sidebar.getChildren().addAll(sidebarTitle, placeholder);
        setLeft(sidebar);
        LOGGER.debug("Left region (sidebar) created");
    }

    /**
     * Creates the center region (main content area).
     * Dynamically switches between grid view and image viewer.
     */
    private void createCenterRegion() {
        // Create grid view as default center content
        gridView = new VirtualizedGridView(appConfig);
        setCenter(gridView.getRoot());
        LOGGER.debug("Center region (grid view) created");
    }

    /**
     * Creates the bottom region (status bar).
     * Displays image count, selected image info, and zoom level.
     */
    private void createBottomRegion() {
        statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new javafx.geometry.Insets(4, 16, 4, 16));
        statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        Label imageCountLabel = new Label("📊 0 images");
        imageCountLabel.setId("status-image-count");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label zoomLabel = new Label("🔍 100%");
        zoomLabel.setId("status-zoom");
        
        Label resolutionLabel = new Label("📐 -- x --");
        resolutionLabel.setId("status-resolution");
        
        statusBar.getChildren().addAll(imageCountLabel, spacer, zoomLabel, resolutionLabel);
        setBottom(statusBar);
        LOGGER.debug("Bottom region (status bar) created");
    }

    /**
     * Creates the right region (optional metadata panel).
     * Displays image metadata when enabled.
     */
    private void createRightRegion() {
        // Initially hidden, can be shown later
        setRight(null);
        LOGGER.debug("Right region (metadata panel) initialized as hidden");
    }

    /**
     * Gets the root node of this view.
     * Since MainView extends BorderPane, it returns itself.
     * 
     * @return This BorderPane instance as the root node
     */
    public BorderPane getRoot() {
        return this;
    }

    /**
     * Gets the application configuration.
     * 
     * @return The AppConfig instance
     */
    public AppConfig getAppConfig() {
        return appConfig;
    }

    /**
     * Switches the center content to grid view mode.
     */
    public void showGridView() {
        LOGGER.debug("Switching to grid view");
        if (gridView != null) {
            setCenter(gridView.getRoot());
        }
    }

    /**
     * Switches the center content to image viewer mode.
     */
    public void showImageViewer() {
        LOGGER.debug("Switching to image viewer");
        if (imageViewer != null) {
            setCenter(imageViewer.getRoot());
        }
    }

    /**
     * Updates the status bar with new information.
     * 
     * @param message The status message to display
     */
    public void updateStatus(String message) {
        LOGGER.debug("Updating status: {}", message);
        if (toolbar != null) {
            // Find the status label and update it
            toolbar.getChildren().stream()
                .filter(node -> node instanceof Label && "toolbar-status".equals(node.getId()))
                .findFirst()
                .ifPresent(node -> ((Label) node).setText(message));
        }
    }
    
    /**
     * Updates the image count in status bar.
     * @param count Number of images
     */
    public void updateImageCount(int count) {
        if (statusBar != null) {
            statusBar.getChildren().stream()
                .filter(node -> node instanceof Label && "status-image-count".equals(node.getId()))
                .findFirst()
                .ifPresent(node -> ((Label) node).setText("📊 " + count + " images"));
        }
    }
}
