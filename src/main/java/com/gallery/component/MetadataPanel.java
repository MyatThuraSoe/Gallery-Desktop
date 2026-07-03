package com.gallery.component;

import com.gallery.metadata.Metadata;
import com.gallery.model.ImageFile;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays image metadata in a structured, scrollable panel.
 */
public class MetadataPanel extends ScrollPane {
    private static final Logger logger = LoggerFactory.getLogger(MetadataPanel.class);

    private final GridPane grid;
    private final Label titleLabel;
    
    public MetadataPanel() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent;");
        
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        
        titleLabel = new Label("Image Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        
        grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(15);
        GridPane.setColumnSpan(grid, 2);
        
        container.getChildren().addAll(titleLabel, grid);
        setContent(container);
        
        logger.debug("MetadataPanel initialized");
    }

    public void displayMetadata(ImageFile imageFile, Metadata metadata) {
        grid.getChildren().clear();
        
        if (imageFile == null) {
            titleLabel.setText("No Image Selected");
            return;
        }

        titleLabel.setText("Image Information");
        
        int row = 0;
        
        addRow("Filename:", imageFile.getFullName(), row++);
        addRow("Path:", imageFile.getPath().toString(), row++);
        addRow("Size:", formatFileSize(imageFile.getFileSize()), row++);
        addRow("Type:", imageFile.getExtension().toUpperCase(), row++);
        
        if (metadata != null) {
            addRow("Dimensions:", metadata.getResolutionString(), row++);
            
            if (metadata.cameraMake() != null || metadata.cameraModel() != null) {
                String camera = (metadata.cameraMake() != null ? metadata.cameraMake() : "") + 
                               " " + (metadata.cameraModel() != null ? metadata.cameraModel() : "");
                addRow("Camera:", camera.trim(), row++);
            }
            if (metadata.iso() != null && metadata.iso() > 0) {
                addRow("ISO:", String.valueOf(metadata.iso().intValue()), row++);
            }
            if (metadata.fNumber() != null) {
                addRow("Aperture:", "f/" + metadata.fNumber(), row++);
            }
            if (metadata.exposureTime() != null) {
                addRow("Shutter:", metadata.exposureTime() + "s", row++);
            }
            if (metadata.focalLength() != null) {
                addRow("Focal Length:", metadata.focalLength() + "mm", row++);
            }
            if (metadata.dateTaken() != null) {
                addRow("Date Taken:", metadata.dateTaken().toString(), row++);
            }
            if (metadata.colorProfile() != null) {
                addRow("Color Profile:", metadata.colorProfile(), row++);
            }
            if (metadata.bitDepth() != null && metadata.bitDepth() > 0) {
                addRow("Bit Depth:", metadata.bitDepth() + "-bit", row++);
            }
            
            if (metadata.hasGpsInfo()) {
                addRow("GPS Latitude:", String.valueOf(metadata.latitude()), row++);
                addRow("GPS Longitude:", String.valueOf(metadata.longitude()), row++);
            }
        } else {
            addRow("Dimensions:", imageFile.getWidth() + " x " + imageFile.getHeight(), row++);
        }
        
        logger.debug("Displayed metadata for {}", imageFile.getFullName());
    }

    private void addRow(String label, String value, int row) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #AAAAAA;");
        
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #FFFFFF; -fx-wrap-text: true;");
        valueNode.setMaxWidth(Double.MAX_VALUE);
        
        GridPane.setHgrow(valueNode, Priority.ALWAYS);
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    public void clear() {
        grid.getChildren().clear();
        titleLabel.setText("Image Information");
    }
}
