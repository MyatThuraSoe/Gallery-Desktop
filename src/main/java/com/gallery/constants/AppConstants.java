package com.gallery.constants;

/**
 * Application-wide constants.
 * 
 * This class centralizes all constant values used throughout the application.
 * Using constants ensures:
 * - Single source of truth
 * - Easy maintenance and updates
 * - Type safety
 * - Avoids magic numbers/strings scattered in code
 * 
 * @author Gallery Team
 * @version 1.0.0
 */
public final class AppConstants {

    // Prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== APPLICATION ====================
    
    /** Application name displayed in window title */
    public static final String APP_NAME = "Gallery Desktop";
    
    /** Application version */
    public static final String APP_VERSION = "1.0.0";
    
    /** Minimum window width in pixels */
    public static final double MIN_WINDOW_WIDTH = 800.0;
    
    /** Minimum window height in pixels */
    public static final double MIN_WINDOW_HEIGHT = 600.0;
    
    /** Default window width in pixels */
    public static final double DEFAULT_WINDOW_WIDTH = 1280.0;
    
    /** Default window height in pixels */
    public static final double DEFAULT_WINDOW_HEIGHT = 800.0;

    // ==================== IMAGE FORMATS ====================
    
    /** Supported image file extensions (lowercase) */
    public static final String[] SUPPORTED_IMAGE_EXTENSIONS = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp", 
        "heic", "tiff", "tif", "svg", "ico"
    };
    
    /** Comma-separated string of supported extensions for file filters */
    public static final String SUPPORTED_EXTENSIONS_FILTER = 
        String.join(",", SUPPORTED_IMAGE_EXTENSIONS);

    // ==================== THUMBNAILS ====================
    
    /** Default thumbnail size in pixels */
    public static final int DEFAULT_THUMBNAIL_SIZE = 256;
    
    /** Minimum thumbnail size in pixels */
    public static final int MIN_THUMBNAIL_SIZE = 64;
    
    /** Maximum thumbnail size in pixels */
    public static final int MAX_THUMBNAIL_SIZE = 512;
    
    /** Thumbnail quality (0.0 - 1.0) */
    public static final double THUMBNAIL_QUALITY = 0.9;
    
    /** Thumbnail file format */
    public static final String THUMBNAIL_FORMAT = "jpg";

    // ==================== CACHE ====================
    
    /** Maximum number of thumbnails in memory cache */
    public static final int MAX_MEMORY_CACHE_SIZE = 500;
    
    /** Maximum disk cache size in megabytes */
    public static final long MAX_DISK_CACHE_SIZE_MB = 500L;
    
    /** Disk cache directory name */
    public static final String CACHE_DIR_NAME = ".gallery_cache";
    
    /** Thumbnail cache subdirectory name */
    public static final String THUMBNAIL_CACHE_SUBDIR = "thumbnails";
    
    /** Metadata cache subdirectory name */
    public static final String METADATA_CACHE_SUBDIR = "metadata";

    // ==================== GRID VIEW ====================
    
    /** Minimum number of columns in grid view */
    public static final int MIN_GRID_COLUMNS = 2;
    
    /** Maximum number of columns in grid view */
    public static final int MAX_GRID_COLUMNS = 10;
    
    /** Default number of columns in grid view */
    public static final int DEFAULT_GRID_COLUMNS = 4;
    
    /** Grid cell padding in pixels */
    public static final double GRID_CELL_PADDING = 8.0;
    
    /** Grid spacing in pixels */
    public static final double GRID_SPACING = 4.0;

    // ==================== ZOOM ====================
    
    /** Minimum zoom level (0.1 = 10%) */
    public static final double MIN_ZOOM_LEVEL = 0.1;
    
    /** Maximum zoom level (10.0 = 1000%) */
    public static final double MAX_ZOOM_LEVEL = 10.0;
    
    /** Zoom step factor for mouse wheel */
    public static final double ZOOM_STEP = 0.1;
    
    /** Zoom animation duration in milliseconds */
    public static final long ZOOM_ANIMATION_DURATION_MS = 200L;
    
    /** Pan animation duration in milliseconds */
    public static final long PAN_ANIMATION_DURATION_MS = 150L;

    // ==================== SLIDESHOW ====================
    
    /** Minimum slideshow interval in seconds */
    public static final int MIN_SLIDESHOW_INTERVAL_SEC = 1;
    
    /** Maximum slideshow interval in seconds */
    public static final int MAX_SLIDESHOW_INTERVAL_SEC = 60;
    
    /** Default slideshow interval in seconds */
    public static final int DEFAULT_SLIDESHOW_INTERVAL_SEC = 5;
    
    /** Slideshow transition duration in milliseconds */
    public static final long SLIDESHOW_TRANSITION_DURATION_MS = 500L;

    // ==================== THREADING ====================
    
    /** Core pool size for image loading executor */
    public static final int IMAGE_LOADING_CORE_POOL_SIZE = 4;
    
    /** Maximum pool size for image loading executor */
    public static final int IMAGE_LOADING_MAX_POOL_SIZE = 8;
    
    /** Keep-alive time for idle threads in seconds */
    public static final long THREAD_KEEP_ALIVE_SECONDS = 60L;
    
    /** Queue capacity for image loading tasks */
    public static final int IMAGE_LOADING_QUEUE_CAPACITY = 100;
    
    /** Core pool size for metadata extraction executor */
    public static final int METADATA_EXTRACTION_CORE_POOL_SIZE = 2;
    
    /** Core pool size for thumbnail generation executor */
    public static final int THUMBNAIL_GENERATION_CORE_POOL_SIZE = 3;

    // ==================== FILE OPERATIONS ====================
    
    /** File operation buffer size in bytes */
    public static final int FILE_BUFFER_SIZE = 8192;
    
    /** Maximum filename length */
    public static final int MAX_FILENAME_LENGTH = 255;

    // ==================== METADATA ====================
    
    /** Unknown metadata value placeholder */
    public static final String UNKNOWN_METADATA = "Unknown";
    
    /** Not available metadata value placeholder */
    public static final String NA_METADATA = "N/A";

    // ==================== UI THEMES ====================
    
    /** Dark theme identifier */
    public static final String THEME_DARK = "dark";
    
    /** Light theme identifier */
    public static final String THEME_LIGHT = "light";
    
    /** Default theme */
    public static final String DEFAULT_THEME = THEME_DARK;

    // ==================== BACKGROUND COLORS ====================
    
    /** Black background hex color */
    public static final String BG_BLACK = "#000000";
    
    /** Gray background hex color */
    public static final String BG_GRAY = "#808080";
    
    /** White background hex color */
    public static final String BG_WHITE = "#FFFFFF";
    
    /** Checkerboard pattern identifier */
    public static final String BG_CHECKERBOARD = "checkerboard";
    
    /** Default background color */
    public static final String DEFAULT_BACKGROUND = BG_BLACK;

    // ==================== SORT OPTIONS ====================
    
    /** Sort by name */
    public static final String SORT_BY_NAME = "name";
    
    /** Sort by date modified */
    public static final String SORT_BY_DATE = "date";
    
    /** Sort by file size */
    public static final String SORT_BY_SIZE = "size";
    
    /** Sort by width */
    public static final String SORT_BY_WIDTH = "width";
    
    /** Sort by height */
    public static final String SORT_BY_HEIGHT = "height";
    
    /** Sort by resolution */
    public static final String SORT_BY_RESOLUTION = "resolution";
    
    /** Sort by extension */
    public static final String SORT_BY_EXTENSION = "extension";
    
    /** Sort randomly */
    public static final String SORT_BY_RANDOM = "random";
    
    /** Default sort field */
    public static final String DEFAULT_SORT_FIELD = SORT_BY_NAME;
    
    /** Ascending sort order */
    public static final String SORT_ASCENDING = "asc";
    
    /** Descending sort order */
    public static final String SORT_DESCENDING = "desc";
    
    /** Default sort order */
    public static final String DEFAULT_SORT_ORDER = SORT_ASCENDING;

    // ==================== FILTER OPTIONS ====================
    
    /** Filter: All images */
    public static final String FILTER_ALL = "all";
    
    /** Filter: Landscape orientation */
    public static final String FILTER_LANDSCAPE = "landscape";
    
    /** Filter: Portrait orientation */
    public static final String FILTER_PORTRAIT = "portrait";
    
    /** Filter: Square images */
    public static final String FILTER_SQUARE = "square";
    
    /** Filter: Favorites only */
    public static final String FILTER_FAVORITES = "favorites";
    
    /** Filter: Large images (> 2MP) */
    public static final String FILTER_LARGE = "large";
    
    /** Filter: Small images (< 0.5MP) */
    public static final String FILTER_SMALL = "small";
    
    /** Filter: Recent images (last 7 days) */
    public static final String FILTER_RECENT = "recent";

    // ==================== SETTINGS KEYS ====================
    
    /** Preferences key: Last opened folder */
    public static final String PREFS_LAST_FOLDER = "last.folder";
    
    /** Preferences key: Window width */
    public static final String PREFS_WINDOW_WIDTH = "window.width";
    
    /** Preferences key: Window height */
    public static final String PREFS_WINDOW_HEIGHT = "window.height";
    
    /** Preferences key: Theme */
    public static final String PREFS_THEME = "ui.theme";
    
    /** Preferences key: Grid columns */
    public static final String PREFS_GRID_COLUMNS = "grid.columns";
    
    /** Preferences key: Thumbnail size */
    public static final String PREFS_THUMBNAIL_SIZE = "thumbnail.size";
    
    /** Preferences key: Sort field */
    public static final String PREFS_SORT_FIELD = "sort.field";
    
    /** Preferences key: Sort order */
    public static final String PREFS_SORT_ORDER = "sort.order";
    
    /** Preferences key: Slideshow interval */
    public static final String PREFS_SLIDESHOW_INTERVAL = "slideshow.interval";
    
    /** Preferences key: Background color */
    public static final String PREFS_BACKGROUND = "viewer.background";
    
    /** Preferences key: Cache size */
    public static final String PREFS_CACHE_SIZE = "cache.size";
    
    /** Preferences node path */
    public static final String PREFS_NODE_PATH = "/com/gallery/desktop";

    // ==================== KEYBOARD SHORTCUTS ====================
    
    /** Help key */
    public static final String KEY_HELP = "F1";
    
    /** Fullscreen toggle key */
    public static final String KEY_FULLSCREEN = "F11";
    
    /** Exit fullscreen key */
    public static final String KEY_EXIT_FULLSCREEN = "ESC";
    
    /** Play/Pause slideshow key */
    public static final String KEY_PLAY_PAUSE = "SPACE";
    
    /** Delete key */
    public static final String KEY_DELETE = "DELETE";
    
    /** First image key */
    public static final String KEY_FIRST_IMAGE = "HOME";
    
    /** Last image key */
    public static final String KEY_LAST_IMAGE = "END";
    
    /** Zoom in modifier */
    public static final String KEY_ZOOM_MODIFIER = "CONTROL";
}
