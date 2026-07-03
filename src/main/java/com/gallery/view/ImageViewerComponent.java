package com.gallery.view;

import com.gallery.model.ImageFile;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * ImageViewerComponent - Professional image viewer with zoom, pan, and navigation.
 * 
 * WHY: Separates complex image viewing logic from the controller.
 * Implements smooth animations, mouse interactions, and zoom optimizations.
 */
public class ImageViewerComponent extends StackPane {
    
    private final ScrollPane scrollPane;
    private final ImageView imageView;
    private final StackPane contentHolder;
    
    private double currentZoom = 1.0;
    private double targetZoom = 1.0;
    private ImageFile currentImage;
    private boolean isPanning = false;
    private double anchorX, anchorY;
    private double translateX = 0, translateY = 0;
    
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 10.0;
    private static final double ZOOM_STEP = 0.1;
    private static final int ANIMATION_DURATION_MS = 200;
    
    public enum BackgroundStyle {
        BLACK("#111111"),
        GRAY("#333333"),
        WHITE("#ffffff"),
        CHECKERBOARD("checkerboard");
        
        private final String value;
        BackgroundStyle(String value) { this.value = value; }
        public String getValue() { return value; }
    }
    
    public ImageViewerComponent() {
        contentHolder = new StackPane();
        contentHolder.setStyle("-fx-background-color: #111111;");
        contentHolder.setAlignment(Pos.CENTER);
        
        imageView = new ImageView();
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        imageView.setCursor(Cursor.OPEN_HAND);
        
        contentHolder.getChildren().add(imageView);
        
        scrollPane = new ScrollPane(contentHolder);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        getChildren().add(scrollPane);
        setStyle("-fx-background-color: #111111;");
        
        setupInteractions();
    }
    
    /**
     * Gets the root node of this viewer (this StackPane itself).
     * @return This ImageViewerComponent instance
     */
    public javafx.scene.Node getRoot() {
        return this;
    }
    
    private void setupInteractions() {
        scrollPane.setOnScroll(event -> {
            double delta = event.getDeltaY();
            if (delta != 0) {
                double zoomFactor = delta > 0 ? (1 + ZOOM_STEP) : (1 - ZOOM_STEP);
                zoom(zoomFactor, event.getSceneX(), event.getSceneY());
            }
            event.consume();
        });
        
        scrollPane.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown() || isPanning) {
                isPanning = true;
                anchorX = translateX;
                anchorY = translateY;
                anchorX -= event.getX();
                anchorY -= event.getY();
                imageView.setCursor(Cursor.CLOSED_HAND);
                event.consume();
            }
        });
        
        scrollPane.setOnMouseDragged(event -> {
            if (isPanning) {
                translateX = anchorX + event.getX();
                translateY = anchorY + event.getY();
                contentHolder.setTranslateX(translateX);
                contentHolder.setTranslateY(translateY);
                event.consume();
            }
        });
        
        scrollPane.setOnMouseReleased(event -> {
            isPanning = false;
            imageView.setCursor(Cursor.OPEN_HAND);
        });
        
        scrollPane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                resetZoom();
            }
        });
    }
    
    public void displayImage(ImageFile imageFile, Image image) {
        this.currentImage = imageFile;
        
        if (image != null) {
            imageView.setImage(image);
            resetZoom();
        }
    }
    
    private void zoom(double factor, double focusX, double focusY) {
        targetZoom = clamp(currentZoom * factor, MIN_ZOOM, MAX_ZOOM);
        
        if (targetZoom != currentZoom) {
            Bounds bounds = imageView.getBoundsInLocal();
            currentZoom = targetZoom;
            imageView.setFitWidth(bounds.getWidth() * currentZoom);
            imageView.setFitHeight(bounds.getHeight() * currentZoom);
        }
    }
    
    public void setZoom(double zoomLevel) {
        targetZoom = clamp(zoomLevel, MIN_ZOOM, MAX_ZOOM);
        animateZoom(targetZoom);
    }
    
    private void animateZoom(double target) {
        TranslateTransition transition = new TranslateTransition(
            Duration.millis(ANIMATION_DURATION_MS), contentHolder);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        
        transition.setOnFinished(e -> {
            currentZoom = target;
            if (imageView.getImage() != null) {
                imageView.setFitWidth(imageView.getImage().getWidth() * currentZoom);
                imageView.setFitHeight(imageView.getImage().getHeight() * currentZoom);
            }
        });
        
        transition.play();
    }
    
    public void resetZoom() {
        if (imageView.getImage() == null) return;
        
        double scaleX = getWidth() / imageView.getImage().getWidth();
        double scaleY = getHeight() / imageView.getImage().getHeight();
        targetZoom = Math.min(scaleX, scaleY) * 0.95;
        
        animateZoom(targetZoom);
        currentZoom = targetZoom;
    }
    
    public void fitToWidth() {
        if (imageView.getImage() == null) return;
        
        targetZoom = getWidth() / imageView.getImage().getWidth();
        animateZoom(targetZoom);
        currentZoom = targetZoom;
    }
    
    public void fitToHeight() {
        if (imageView.getImage() == null) return;
        
        targetZoom = getHeight() / imageView.getImage().getHeight();
        animateZoom(targetZoom);
        currentZoom = targetZoom;
    }
    
    public void actualSize() {
        targetZoom = 1.0;
        animateZoom(targetZoom);
        currentZoom = targetZoom;
    }
    
    public void rotate(double degrees) {
        imageView.setRotate(imageView.getRotate() + degrees);
    }
    
    public void flipHorizontal() {
        imageView.setScaleX(-imageView.getScaleX());
    }
    
    public void flipVertical() {
        imageView.setScaleY(-imageView.getScaleY());
    }
    
    public void setBackgroundStyle(BackgroundStyle style) {
        switch (style) {
            case BLACK -> contentHolder.setStyle("-fx-background-color: #111111;");
            case GRAY -> contentHolder.setStyle("-fx-background-color: #333333;");
            case WHITE -> contentHolder.setStyle("-fx-background-color: #ffffff;");
            case CHECKERBOARD -> contentHolder.setStyle("-fx-background-color: #111111;");
        }
    }
    
    public double getCurrentZoom() {
        return currentZoom;
    }
    
    public ImageFile getCurrentImage() {
        return currentImage;
    }
    
    public void clear() {
        imageView.setImage(null);
        currentImage = null;
        currentZoom = 1.0;
        translateX = 0;
        translateY = 0;
        contentHolder.setTranslateX(0);
        contentHolder.setTranslateY(0);
    }
    
    /**
     * Clamps a value between min and max.
     * Helper method since Math.clamp is not available in Java 17.
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
