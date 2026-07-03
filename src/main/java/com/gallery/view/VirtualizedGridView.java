package com.gallery.view;

import com.gallery.component.ThumbnailCard;
import com.gallery.dto.ThumbnailDTO;
import com.gallery.model.ImageFile;
import com.gallery.service.ThumbnailService;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * VirtualizedGridView - High-performance grid view with virtualized rendering.
 * 
 * WHY: Standard JavaFX controls struggle with 100K+ images. This custom implementation
 * only renders visible thumbnails plus a small buffer, enabling smooth scrolling.
 */
public class VirtualizedGridView extends ScrollPane {
    
    private final VBox container;
    private final FlowPane[] rowContainers;
    
    private int columns = 5;
    private double thumbnailSize = 128;
    private int bufferSize = 5;
    
    private List<ImageFile> images;
    private ThumbnailService thumbnailService;
    private Consumer<ImageFile> onImageSelected;
    private Consumer<ImageFile> onImageDoubleClicked;
    
    private int firstVisibleRow = 0;
    private int lastVisibleRow = 0;
    
    public VirtualizedGridView() {
        container = new VBox(10);
        container.setStyle("-fx-padding: 10px;");
        
        rowContainers = new FlowPane[100];
        
        setContent(container);
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");
        
        vvalueProperty().addListener((obs, oldVal, newVal) -> updateVisibleRows());
        widthProperty().addListener((obs, oldVal, newVal) -> updateLayout());
    }
    
    public void setData(List<ImageFile> images, ThumbnailService thumbnailService) {
        this.images = images;
        this.thumbnailService = thumbnailService;
        
        container.getChildren().clear();
        
        int totalRows = (int) Math.ceil((double) images.size() / columns);
        createRows(0, Math.min(bufferSize * 2, totalRows));
        
        Platform.runLater(this::updateVisibleRows);
    }
    
    private void createRows(int startRow, int endRow) {
        for (int row = startRow; row < endRow && row < rowContainers.length; row++) {
            if (rowContainers[row] == null) {
                FlowPane rowPane = new FlowPane(10, 10);
                rowPane.setPrefWrapLength(getWidth() - 20);
                rowContainers[row] = rowPane;
                container.getChildren().add(rowPane);
                
                populateRow(row);
            }
        }
    }
    
    private void populateRow(int row) {
        if (images == null || rowContainers[row] == null) return;
        
        FlowPane rowPane = rowContainers[row];
        rowPane.getChildren().clear();
        
        int startIndex = row * columns;
        int endIndex = Math.min(startIndex + columns, images.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            ImageFile image = images.get(i);
            ThumbnailCard card = createThumbnailCard(image);
            rowPane.getChildren().add(card);
        }
    }
    
    private ThumbnailCard createThumbnailCard(ImageFile image) {
        ThumbnailCard card = new ThumbnailCard();
        card.setSize(thumbnailSize);
        
        if (thumbnailService != null) {
            thumbnailService.loadThumbnailAsync(image).thenAccept(thumbnail -> {
                if (thumbnail != null) {
                    Platform.runLater(() -> card.updateThumbnail(thumbnail.image()));
                }
            });
        }
        
        card.setOnMouseClicked(event -> {
            deselectAll();
            card.setSelected(true);
            
            if (onImageSelected != null) {
                onImageSelected.accept(image);
            }
            
            if (event.getClickCount() == 2 && onImageDoubleClicked != null) {
                onImageDoubleClicked.accept(image);
            }
        });
        
        return card;
    }
    
    private void updateVisibleRows() {
        if (images == null || images.isEmpty()) return;
        
        double scrollY = getVvalue() * (container.getHeight() - getHeight());
        int approximateRow = (int) (scrollY / (thumbnailSize + 10));
        
        int newFirstRow = Math.max(0, approximateRow - bufferSize);
        int newLastRow = Math.min(
            (int) Math.ceil((double) images.size() / columns),
            approximateRow + bufferSize * 2
        );
        
        if (Math.abs(newFirstRow - firstVisibleRow) > bufferSize / 2) {
            firstVisibleRow = newFirstRow;
            lastVisibleRow = newLastRow;
            
            createRows(firstVisibleRow, lastVisibleRow + 1);
        }
    }
    
    private void updateLayout() {
        if (rowContainers[0] != null) {
            for (FlowPane row : rowContainers) {
                if (row != null) {
                    row.setPrefWrapLength(getWidth() - 20);
                }
            }
        }
    }
    
    private void deselectAll() {
        for (FlowPane row : rowContainers) {
            if (row != null) {
                for (var node : row.getChildren()) {
                    if (node instanceof ThumbnailCard card) {
                        card.setSelected(false);
                    }
                }
            }
        }
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
        updateLayout();
        refresh();
    }
    
    public void setThumbnailSize(double size) {
        this.thumbnailSize = size;
        refresh();
    }
    
    public void refresh() {
        if (images != null) {
            container.getChildren().clear();
            for (int i = 0; i < rowContainers.length; i++) {
                rowContainers[i] = null;
            }
            
            int totalRows = (int) Math.ceil((double) images.size() / columns);
            createRows(0, Math.min(bufferSize * 2, totalRows));
            updateVisibleRows();
        }
    }
    
    public void onImageSelected(Consumer<ImageFile> callback) {
        this.onImageSelected = callback;
    }
    
    public void onImageDoubleClicked(Consumer<ImageFile> callback) {
        this.onImageDoubleClicked = callback;
    }
    
    public void scrollToIndex(int index) {
        if (images == null || index < 0 || index >= images.size()) return;
        
        int row = index / columns;
        double scrollPosition = (double) row / 
            Math.ceil((double) images.size() / columns);
        
        setVvalue(scrollPosition);
    }
}
