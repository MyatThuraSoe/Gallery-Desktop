package com.gallery.component;

import com.gallery.dto.ThumbnailDTO;
import com.gallery.model.ImageFile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * ThumbnailCard - Reusable component for displaying image thumbnails in the grid.
 * 
 * WHY: Encapsulates thumbnail rendering logic, making the grid view cleaner and more maintainable.
 * Follows Single Responsibility Principle - only handles thumbnail display.
 */
public class ThumbnailCard extends VBox {
    
    private final ImageView thumbnailView;
    private final Label filenameLabel;
    private final Label resolutionLabel;
    private ImageFile imageFile;
    private boolean selected = false;
    
    private boolean showFilename = true;
    private boolean showResolution = false;
    private ThumbnailMode mode = ThumbnailMode.SQUARE;
    
    public enum ThumbnailMode {
        SQUARE,
        ORIGINAL_ASPECT,
        ROUNDED
    }
    
    public ThumbnailCard() {
        super(4);
        getStyleClass().add("thumbnail-card");
        setAlignment(Pos.CENTER);
        setPadding(new Insets(4));
        
        thumbnailView = new ImageView();
        thumbnailView.setFitWidth(128);
        thumbnailView.setFitHeight(128);
        thumbnailView.setPreserveRatio(false);
        thumbnailView.setSmooth(true);
        
        filenameLabel = new Label();
        filenameLabel.getStyleClass().add("thumbnail-label");
        filenameLabel.setMaxWidth(128);
        
        resolutionLabel = new Label();
        resolutionLabel.getStyleClass().add("thumbnail-label");
        resolutionLabel.setMaxWidth(128);
        
        getChildren().addAll(thumbnailView, filenameLabel, resolutionLabel);
        applyMode();
    }
    
    public void bind(ImageFile imageFile, ThumbnailDTO thumbnail) {
        this.imageFile = imageFile;
        
        if (thumbnail != null && thumbnail.image() != null) {
            thumbnailView.setImage(thumbnail.image());
        }
        
        updateLabels();
    }
    
    public void updateThumbnail(Image image) {
        if (image != null) {
            thumbnailView.setImage(image);
        }
    }
    
    private void updateLabels() {
        if (imageFile == null) return;
        
        if (showFilename) {
            filenameLabel.setText(truncateFilename(imageFile.getFileName()));
            filenameLabel.setVisible(true);
            filenameLabel.setManaged(true);
        } else {
            filenameLabel.setVisible(false);
            filenameLabel.setManaged(false);
        }
        
        if (showResolution) {
            resolutionLabel.setText(imageFile.getWidth() + "×" + imageFile.getHeight());
            resolutionLabel.setVisible(true);
            resolutionLabel.setManaged(true);
        } else {
            resolutionLabel.setVisible(false);
            resolutionLabel.setManaged(false);
        }
    }
    
    private String truncateFilename(String filename) {
        if (filename.length() <= 20) {
            return filename;
        }
        return filename.substring(0, 17) + "...";
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            getStyleClass().add("selected");
        } else {
            getStyleClass().remove("selected");
        }
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public ImageFile getImageFile() {
        return imageFile;
    }
    
    public void setShowFilename(boolean show) {
        this.showFilename = show;
        updateLabels();
    }
    
    public void setShowResolution(boolean show) {
        this.showResolution = show;
        updateLabels();
    }
    
    public void setMode(ThumbnailMode mode) {
        this.mode = mode;
        applyMode();
    }
    
    private void applyMode() {
        switch (mode) {
            case SQUARE -> {
                thumbnailView.setPreserveRatio(false);
                thumbnailView.setFitWidth(128);
                thumbnailView.setFitHeight(128);
            }
            case ORIGINAL_ASPECT -> {
                thumbnailView.setPreserveRatio(true);
                thumbnailView.setFitWidth(128);
                thumbnailView.setFitHeight(Double.MAX_VALUE);
            }
            case ROUNDED -> {
                thumbnailView.setPreserveRatio(false);
                thumbnailView.setFitWidth(128);
                thumbnailView.setFitHeight(128);
                
                CornerRadii radii = new CornerRadii(8);
                BackgroundFill fill = new BackgroundFill(Color.TRANSPARENT, radii, null);
                setBackground(new Background(fill));
            }
        }
    }
    
    public void setSize(double size) {
        thumbnailView.setFitWidth(size);
        thumbnailView.setFitHeight(mode == ThumbnailMode.SQUARE ? size : Double.MAX_VALUE);
        filenameLabel.setMaxWidth(size);
        resolutionLabel.setMaxWidth(size);
    }
}
