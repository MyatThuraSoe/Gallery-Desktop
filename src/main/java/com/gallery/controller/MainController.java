package com.gallery.controller;

import com.gallery.config.AppConfig;
import com.gallery.model.ImageFile;
import com.gallery.service.FolderScannerService;
import com.gallery.service.ImageLoaderService;
import com.gallery.service.ThumbnailService;
import com.gallery.view.ImageViewerComponent;
import com.gallery.view.VirtualizedGridView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * MainController - Coordinates all UI components and user interactions.
 * 
 * WHY: Central controller following MVC pattern. Keeps business logic in services,
 * UI rendering in views, and coordination here.
 */
public class MainController implements Initializable {
    
    @FXML private BorderPane rootPane;
    @FXML private Button btnOpenFolder, btnPrevious, btnNext, btnRotateLeft, btnRotateRight;
    @FXML private Button btnFlip, btnZoomOut, btnZoomIn, btnFitWidth, btnFitScreen;
    @FXML private Button btnSlideshow, btnFullscreen, btnPrevOverlay, btnNextOverlay;
    @FXML private Label lblZoomLevel, statusImageCount, statusSelected, statusZoom, statusMemory;
    @FXML private TextField txtSearch;
    @FXML private TreeView<File> folderTree;
    @FXML private CheckBox chkLandscape, chkPortrait, chkSquare, chkFavorites;
    @FXML private ComboBox<String> cmbGridLayout;
    @FXML private Slider sliderThumbnailSize;
    @FXML private ScrollPane gridScroll;
    @FXML private FlowPane gridContainer;
    @FXML private StackPane contentPane, imageViewer;
    @FXML private ImageViewerComponent mainImageView;
    @FXML private VBox metadataPanel;
    @FXML private Label metaFilename, metaResolution, metaFileSize, metaDateTaken;
    @FXML private Label metaCamera, metaISO, metaLens, metaGPS;
    
    private FolderScannerService folderScanner;
    private ThumbnailService thumbnailService;
    private ImageLoaderService imageLoader;
    
    private VirtualizedGridView gridView;
    
    private List<ImageFile> currentImages;
    private List<ImageFile> filteredImages;
    private ImageFile currentImage;
    private int currentIndex = 0;
    private File currentFolder;
    private boolean isViewerMode = false;
    
    private AppConfig settings;
    
    private static final String[] GRID_LAYOUTS = {
        "Auto", "2 Columns", "3 Columns", "4 Columns", 
        "5 Columns", "6 Columns", "8 Columns", "10 Columns"
    };
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        folderScanner = new FolderScannerService();
        thumbnailService = new ThumbnailService();
        imageLoader = new ImageLoaderService();
        
        settings = new AppConfig();
        settings.load();
        
        setupGridView();
        initializeComboBoxes();
        setupEventHandlers();
        setupKeyboardShortcuts();
        applySettings();
        startMemoryMonitor();
    }
    
    private void setupGridView() {
        gridView = new VirtualizedGridView();
        gridScroll.setContent(gridView);
        
        gridView.onImageSelected(this::onImageSelected);
        gridView.onImageDoubleClicked(this::openImageViewer);
    }
    
    private void initializeComboBoxes() {
        cmbGridLayout.setItems(FXCollections.observableArrayList(GRID_LAYOUTS));
        cmbGridLayout.setValue("5 Columns");
        cmbGridLayout.setOnAction(e -> updateGridColumnCount());
    }
    
    private void setupEventHandlers() {
        btnOpenFolder.setOnAction(e -> openFolderDialog());
        btnPrevious.setOnAction(e -> navigatePrevious());
        btnNext.setOnAction(e -> navigateNext());
        btnRotateLeft.setOnAction(e -> rotateImage(-90));
        btnRotateRight.setOnAction(e -> rotateImage(90));
        btnFlip.setOnAction(e -> flipImage());
        btnZoomOut.setOnAction(e -> adjustZoom(-0.1));
        btnZoomIn.setOnAction(e -> adjustZoom(0.1));
        btnFitWidth.setOnAction(e -> fitToWidth());
        btnFitScreen.setOnAction(e -> fitToScreen());
        btnSlideshow.setOnAction(e -> toggleSlideshow());
        btnFullscreen.setOnAction(e -> toggleFullscreen());
        
        btnPrevOverlay.setOnAction(e -> navigatePrevious());
        btnNextOverlay.setOnAction(e -> navigateNext());
        
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterImages(newVal));
        
        sliderThumbnailSize.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (gridView != null) {
                gridView.setThumbnailSize(newVal.doubleValue());
            }
        });
        
        chkLandscape.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        chkPortrait.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        chkSquare.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        chkFavorites.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        folderTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                scanFolder(newVal.getValue());
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        rootPane.setOnKeyPressed(this::handleKeyPress);
        rootPane.setFocusTraversable(true);
    }
    
    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        
        switch (code) {
            case LEFT -> navigatePrevious();
            case RIGHT -> navigateNext();
            case HOME -> goToFirstImage();
            case END -> goToLastImage();
            case DELETE -> deleteCurrentImage();
            case SPACE -> toggleSlideshow();
            case F11 -> toggleFullscreen();
            case ESCAPE -> exitViewerMode();
            case Z -> { if (event.isControlDown()) adjustZoom(0.1); }
            case X -> { if (event.isControlDown()) adjustZoom(-0.1); }
            case R -> rotateImage(90);
            case F -> flipImage();
            case W -> fitToWidth();
            case H -> fitToHeight();
            case DIGIT1 -> setZoom(1.0);
            case DIGIT2 -> setZoom(2.0);
            case ADD -> adjustZoom(0.1);
            case SUBTRACT -> adjustZoom(-0.1);
        }
        event.consume();
    }
    
    private void openFolderDialog() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Image Folder");
        chooser.setInitialDirectory(currentFolder != null ? currentFolder : new File(System.getProperty("user.home")));
        
        File selected = chooser.showDialog(rootPane.getScene().getWindow());
        if (selected != null) {
            scanFolder(selected);
        }
    }
    
    private void scanFolder(File folder) {
        currentFolder = folder;
        statusImageCount.setText("Scanning...");
        
        folderScanner.scanFolderAsync(folder.toPath()).thenAccept(result -> {
            Platform.runLater(() -> {
                currentImages = result;
                filteredImages = result;
                
                gridView.setData(filteredImages, thumbnailService);
                statusImageCount.setText(filteredImages.size() + " images");
                updateFolderTree(folder);
            });
        });
    }
    
    private void updateFolderTree(File root) {
        TreeItem<File> rootItem = new TreeItem<>(root);
        rootItem.setExpanded(true);
        
        File[] subfolders = root.listFiles(File::isDirectory);
        if (subfolders != null) {
            for (File subfolder : subfolders) {
                TreeItem<File> item = new TreeItem<>(subfolder);
                rootItem.getChildren().add(item);
            }
        }
        
        folderTree.setRoot(rootItem);
    }
    
    private void onImageSelected(ImageFile image) {
        currentImage = image;
        currentIndex = filteredImages.indexOf(image);
        
        statusSelected.setText(image.getFileName());
        updateMetadataDisplay(image);
        preloadAdjacentImages();
    }
    
    private void openImageViewer(ImageFile image) {
        currentImage = image;
        currentIndex = filteredImages.indexOf(image);
        isViewerMode = true;
        
        imageLoader.loadImageAsync(image).thenAccept(img -> {
            Platform.runLater(() -> {
                mainImageView.displayImage(image, img);
            });
        });
        
        gridView.setVisible(false);
        gridView.setManaged(false);
        imageViewer.setVisible(true);
        imageViewer.setManaged(true);
        
        btnPrevOverlay.setVisible(true);
        btnNextOverlay.setVisible(true);
        
        updateZoomLabel();
    }
    
    private void exitViewerMode() {
        isViewerMode = false;
        
        imageViewer.setVisible(false);
        imageViewer.setManaged(false);
        gridView.setVisible(true);
        gridView.setManaged(true);
        
        btnPrevOverlay.setVisible(false);
        btnNextOverlay.setVisible(false);
    }
    
    private void navigatePrevious() {
        if (filteredImages.isEmpty()) return;
        
        currentIndex = (currentIndex - 1 + filteredImages.size()) % filteredImages.size();
        ImageFile image = filteredImages.get(currentIndex);
        
        if (isViewerMode) {
            imageLoader.loadImageAsync(image).thenAccept(img -> {
                Platform.runLater(() -> mainImageView.displayImage(image, img));
            });
        } else {
            gridView.scrollToIndex(currentIndex);
            onImageSelected(image);
        }
    }
    
    private void navigateNext() {
        if (filteredImages.isEmpty()) return;
        
        currentIndex = (currentIndex + 1) % filteredImages.size();
        ImageFile image = filteredImages.get(currentIndex);
        
        if (isViewerMode) {
            imageLoader.loadImageAsync(image).thenAccept(img -> {
                Platform.runLater(() -> mainImageView.displayImage(image, img));
            });
        } else {
            gridView.scrollToIndex(currentIndex);
            onImageSelected(image);
        }
    }
    
    private void goToFirstImage() {
        if (filteredImages.isEmpty()) return;
        currentIndex = 0;
        onImageSelected(filteredImages.get(0));
    }
    
    private void goToLastImage() {
        if (filteredImages.isEmpty()) return;
        currentIndex = filteredImages.size() - 1;
        onImageSelected(filteredImages.get(currentIndex));
    }
    
    private void rotateImage(double degrees) {
        if (isViewerMode) {
            mainImageView.rotate(degrees);
        }
    }
    
    private void flipImage() {
        if (isViewerMode) {
            mainImageView.flipHorizontal();
        }
    }
    
    private void adjustZoom(double delta) {
        if (isViewerMode) {
            double newZoom = mainImageView.getCurrentZoom() + delta;
            mainImageView.setZoom(newZoom);
            updateZoomLabel();
        }
    }
    
    private void setZoom(double level) {
        if (isViewerMode) {
            mainImageView.setZoom(level);
            updateZoomLabel();
        }
    }
    
    private void fitToWidth() {
        if (isViewerMode) {
            mainImageView.fitToWidth();
            updateZoomLabel();
        }
    }
    
    private void fitToHeight() {
        if (isViewerMode) {
            mainImageView.fitToHeight();
            updateZoomLabel();
        }
    }
    
    private void fitToScreen() {
        if (isViewerMode) {
            mainImageView.resetZoom();
            updateZoomLabel();
        }
    }
    
    private void updateZoomLabel() {
        if (isViewerMode) {
            int zoomPercent = (int) (mainImageView.getCurrentZoom() * 100);
            lblZoomLevel.setText(zoomPercent + "%");
            statusZoom.setText("Zoom: " + zoomPercent + "%");
        }
    }
    
    private void toggleSlideshow() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Slideshow");
        alert.setHeaderText(null);
        alert.setContentText("Slideshow feature coming soon!");
        alert.showAndWait();
    }
    
    private void toggleFullscreen() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
    
    private void deleteCurrentImage() {
        if (currentImage == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Image");
        alert.setHeaderText("Delete " + currentImage.getFileName() + "?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Delete: " + currentImage.getPath());
            }
        });
    }
    
    private void filterImages(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredImages = currentImages;
        } else {
            String lowerSearch = searchText.toLowerCase();
            filteredImages = currentImages.stream()
                .filter(img -> img.getFileName().toLowerCase().contains(lowerSearch))
                .toList();
        }
        
        gridView.refresh();
        statusImageCount.setText(filteredImages.size() + " images");
    }
    
    private void applyFilters() {
        Predicate<ImageFile> filter = img -> true;
        
        if (chkLandscape.isSelected()) {
            filter = filter.and(img -> img.getWidth() > img.getHeight());
        }
        if (chkPortrait.isSelected()) {
            filter = filter.and(img -> img.getHeight() > img.getWidth());
        }
        if (chkSquare.isSelected()) {
            filter = filter.and(img -> img.getWidth() == img.getHeight());
        }
        
        filteredImages = currentImages.stream().filter(filter).toList();
        gridView.refresh();
        statusImageCount.setText(filteredImages.size() + " images");
    }
    
    private void updateMetadataDisplay(ImageFile image) {
        metaFilename.setText("File: " + image.getFullName());
        metaResolution.setText("Resolution: " + image.getWidth() + " × " + image.getHeight());
        metaFileSize.setText("Size: " + formatFileSize(image.getFileSize()));
        metaDateTaken.setText("Modified: " + image.getLastModified());
        
        metaCamera.setText("Camera: -");
        metaISO.setText("ISO: -");
        metaLens.setText("Lens: -");
        metaGPS.setText("GPS: -");
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private void preloadAdjacentImages() {
        if (currentIndex + 1 < filteredImages.size()) {
            ImageFile next = filteredImages.get(currentIndex + 1);
            imageLoader.loadImageAsync(next).thenAccept(img -> {});
        }
        
        if (currentIndex - 1 >= 0) {
            ImageFile prev = filteredImages.get(currentIndex - 1);
            imageLoader.loadImageAsync(prev).thenAccept(img -> {});
        }
    }
    
    private void updateGridColumnCount() {
        String selected = cmbGridLayout.getValue();
        if (selected == null) return;
        
        int columns = switch (selected) {
            case "2 Columns" -> 2;
            case "3 Columns" -> 3;
            case "4 Columns" -> 4;
            case "5 Columns" -> 5;
            case "6 Columns" -> 6;
            case "8 Columns" -> 8;
            case "10 Columns" -> 10;
            default -> 5;
        };
        
        gridView.setColumns(columns);
    }
    
    private void applySettings() {
        cmbGridLayout.setValue(settings.getGridLayout());
        sliderThumbnailSize.setValue(settings.getThumbnailSize());
        
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setWidth(settings.getWindowWidth());
        stage.setHeight(settings.getWindowHeight());
    }
    
    private void startMemoryMonitor() {
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (now - lastUpdate > 2_000_000_000L) {
                    Runtime runtime = Runtime.getRuntime();
                    long used = runtime.totalMemory() - runtime.freeMemory();
                    statusMemory.setText("Memory: " + formatFileSize(used));
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }
    
    public void saveSettings() {
        settings.setGridLayout(cmbGridLayout.getValue());
        settings.setThumbnailSize(sliderThumbnailSize.getValue());
        
        Stage stage = (Stage) rootPane.getScene().getWindow();
        settings.setWindowWidth(stage.getWidth());
        settings.setWindowHeight(stage.getHeight());
        
        settings.save();
    }
}
