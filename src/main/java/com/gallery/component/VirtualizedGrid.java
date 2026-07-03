package com.gallery.component;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Virtualized grid container for efficient rendering of large image collections.
 * 
 * WHY: Rendering 100,000+ thumbnails simultaneously would consume excessive memory and cause UI lag.
 * Virtualization only renders visible items plus a small buffer, dramatically improving performance.
 * This implementation uses ScrollPane with dynamic content based on scroll position.
 */
public class VirtualizedGrid extends ScrollPane {
    
    private static final Logger LOG = LoggerFactory.getLogger(VirtualizedGrid.class);
    
    /**
     * Number of items to render outside the visible area (buffer).
     * Higher values reduce flickering during scrolling but use more memory.
     */
    private static final int BUFFER_SIZE = 5;
    
    /**
     * Total number of items in the dataset.
     */
    private int totalItemCount = 0;
    
    /**
     * Number of columns in the grid.
     */
    private int columnCount = 4;
    
    /**
     * Width of each cell in pixels.
     */
    private double cellWidth = 200;
    
    /**
     * Height of each cell in pixels.
     */
    private double cellHeight = 250;
    
    /**
     * Spacing between cells in pixels.
     */
    private double spacing = 8;
    
    /**
     * Callback invoked when an item should be rendered at a specific index.
     */
    private Consumer<Integer> renderItemCallback;
    
    /**
     * Callback invoked when an item should be recycled (removed) at a specific index.
     */
    private Consumer<Integer> recycleItemCallback;
    
    /**
     * FlowPane that holds the visible cells.
     */
    private final FlowPane flowPane;
    
    /**
     * Currently rendered item indices.
     */
    private int firstVisibleIndex = 0;
    private int lastVisibleIndex = 0;
    
    /**
     * Creates a new VirtualizedGrid with default settings.
     */
    public VirtualizedGrid() {
        this.flowPane = new FlowPane();
        this.flowPane.setPrefWrapLength(800); // Initial width
        
        setContent(flowPane);
        setFitToWidth(true);
        
        // Listen to scroll position changes
        vvalueProperty().addListener((obs, oldVal, newVal) -> updateVisibleItems());
        widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                recalculateColumns();
                updateVisibleItems();
            }
        });
        
        LOG.debug("VirtualizedGrid initialized");
    }
    
    /**
     * Sets the total number of items to display.
     * This creates the virtual space without actually creating all nodes.
     * 
     * @param count Total item count
     */
    public void setTotalItemCount(int count) {
        this.totalItemCount = count;
        updateContentSize();
        updateVisibleItems();
        LOG.debug("Total item count set to: {}", count);
    }
    
    /**
     * Sets the callback for rendering an item at a given index.
     * 
     * @param callback Function that receives the index and creates/updates the UI element
     */
    public void setRenderItemCallback(Consumer<Integer> callback) {
        this.renderItemCallback = callback;
    }
    
    /**
     * Sets the callback for recycling an item at a given index.
     * 
     * @param callback Function that receives the index and cleans up the UI element
     */
    public void setRecycleItemCallback(Consumer<Integer> callback) {
        this.recycleItemCallback = callback;
    }
    
    /**
     * Sets the cell dimensions.
     * 
     * @param width Cell width in pixels
     * @param height Cell height in pixels
     */
    public void setCellSize(double width, double height) {
        this.cellWidth = width;
        this.cellHeight = height;
        recalculateColumns();
        updateContentSize();
        updateVisibleItems();
        LOG.debug("Cell size set to: {}x{}", width, height);
    }
    
    /**
     * Sets the number of columns explicitly.
     * 
     * @param columns Number of columns
     */
    public void setColumnCount(int columns) {
        this.columnCount = columns;
        updateContentSize();
        updateVisibleItems();
        LOG.debug("Column count set to: {}", columns);
    }
    
    /**
     * Sets the spacing between cells.
     * 
     * @param spacing Spacing in pixels
     */
    public void setSpacing(double spacing) {
        this.spacing = spacing;
        flowPane.setHgap(spacing);
        flowPane.setVgap(spacing);
        updateContentSize();
        updateVisibleItems();
    }
    
    /**
     * Recalculates the number of columns based on available width.
     */
    private void recalculateColumns() {
        double availableWidth = getWidth() - getPadding().getLeft() - getPadding().getRight();
        if (availableWidth > 0 && cellWidth > 0) {
            this.columnCount = Math.max(1, (int) ((availableWidth + spacing) / (cellWidth + spacing)));
            flowPane.setPrefWrapLength(availableWidth);
        }
    }
    
    /**
     * Updates the content size to create the virtual scrollable area.
     */
    private void updateContentSize() {
        if (totalItemCount == 0 || columnCount == 0) {
            flowPane.setPrefWidth(0);
            flowPane.setPrefHeight(0);
            return;
        }
        
        int rowCount = (int) Math.ceil((double) totalItemCount / columnCount);
        double prefWidth = columnCount * cellWidth + (columnCount - 1) * spacing;
        double prefHeight = rowCount * cellHeight + (rowCount - 1) * spacing;
        
        flowPane.setPrefWidth(prefWidth);
        flowPane.setPrefHeight(prefHeight);
    }
    
    /**
     * Updates which items are visible based on current scroll position.
     * Only renders items within the viewport plus buffer zone.
     */
    private void updateVisibleItems() {
        if (totalItemCount == 0 || renderItemCallback == null) {
            return;
        }
        
        double scrollY = getVvalue() * (flowPane.getPrefHeight() - getHeight());
        int newFirstVisible = Math.max(0, (int) (scrollY / (cellHeight + spacing)) * columnCount - BUFFER_SIZE * columnCount);
        int newLastVisible = Math.min(totalItemCount - 1, newFirstVisible + (int) Math.ceil(getHeight() / (cellHeight + spacing)) * columnCount + BUFFER_SIZE * columnCount);
        
        // Check if visible range changed significantly
        if (Math.abs(newFirstVisible - firstVisibleIndex) < columnCount && 
            Math.abs(newLastVisible - lastVisibleIndex) < columnCount) {
            return; // No significant change, skip update
        }
        
        int oldFirst = firstVisibleIndex;
        int oldLast = lastVisibleIndex;
        firstVisibleIndex = newFirstVisible;
        lastVisibleIndex = newLastVisible;
        
        // Recycle items that are no longer visible
        for (int i = oldFirst; i < oldLast && i < totalItemCount; i++) {
            if (i < firstVisibleIndex || i > lastVisibleIndex) {
                recycleItem(i);
            }
        }
        
        // Render newly visible items
        for (int i = firstVisibleIndex; i <= lastVisibleIndex && i < totalItemCount; i++) {
            if (i < oldFirst || i > oldLast) {
                renderItem(i);
            }
        }
        
        LOG.trace("Visible range updated: {}-{} (total: {})", firstVisibleIndex, lastVisibleIndex, totalItemCount);
    }
    
    /**
     * Renders an item at the specified index.
     * 
     * @param index Item index to render
     */
    private void renderItem(int index) {
        if (renderItemCallback != null) {
            renderItemCallback.accept(index);
        }
    }
    
    /**
     * Recycles an item at the specified index.
     * 
     * @param index Item index to recycle
     */
    private void recycleItem(int index) {
        if (recycleItemCallback != null) {
            recycleItemCallback.accept(index);
        }
    }
    
    /**
     * Refreshes the entire grid.
     * Call this when data changes significantly.
     */
    public void refresh() {
        flowPane.getChildren().clear();
        updateVisibleItems();
        LOG.debug("Grid refreshed");
    }
    
    /**
     * Scrolls to a specific item index.
     * 
     * @param index Index to scroll to
     */
    public void scrollToIndex(int index) {
        if (totalItemCount == 0 || index < 0 || index >= totalItemCount) {
            return;
        }
        
        int row = index / columnCount;
        double scrollY = row * (cellHeight + spacing);
        double maxScroll = flowPane.getPrefHeight() - getHeight();
        
        if (maxScroll > 0) {
            double scrollPercent = Math.min(1.0, scrollY / maxScroll);
            setVvalue(scrollPercent);
        }
        
        LOG.debug("Scrolled to index: {}", index);
    }
    
    /**
     * Gets the currently visible item indices.
     * 
     * @return Array with [firstVisible, lastVisible]
     */
    public int[] getVisibleRange() {
        return new int[]{firstVisibleIndex, lastVisibleIndex};
    }
}
