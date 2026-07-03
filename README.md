# Gallery Desktop Application

Professional Image Gallery Desktop Application built with Java 21 and JavaFX 24.

## Features

- **Folder Browser**: Select, browse, and manage image folders
- **Grid View**: Virtualized, lazy-loaded thumbnail grid with adjustable layouts
- **Image Viewer**: Full-featured viewer with zoom, pan, rotate, flip capabilities
- **Slideshow**: Auto-play with configurable transitions and intervals
- **Search & Filter**: Instant filename search and advanced filtering
- **Sort**: Multiple sort options (name, date, size, resolution, etc.)
- **Metadata**: EXIF, IPTC, XMP metadata extraction and display
- **File Operations**: Rename, delete, move, copy, duplicate
- **Performance**: Background loading, caching, handles 100,000+ images

## Tech Stack

- Java 21 LTS
- JavaFX 24
- Maven
- SQLite (optional)
- Thumbnailator
- Metadata Extractor
- JUnit 5

## Project Structure

```
src
├── main
│   ├── java
│   │   └── com.gallery
│   │       ├── app/           # Application entry point
│   │       ├── config/        # Configuration classes
│   │       ├── constants/     # Application constants
│   │       ├── controller/    # MVC Controllers
│   │       ├── service/       # Business logic services
│   │       ├── model/         # Domain models
│   │       ├── repository/    # Data access layer
│   │       ├── view/          # UI components
│   │       ├── component/     # Reusable UI components
│   │       ├── cache/         # Memory and disk caching
│   │       ├── thumbnail/     # Thumbnail generation
│   │       ├── metadata/      # Metadata extraction
│   │       ├── slideshow/     # Slideshow functionality
│   │       ├── zoom/          # Zoom operations
│   │       ├── layout/        # Layout managers
│   │       ├── file/          # File operations
│   │       ├── navigation/    # Navigation logic
│   │       ├── search/        # Search functionality
│   │       ├── sort/          # Sorting logic
│   │       ├── filter/        # Filtering logic
│   │       ├── theme/         # Theme management
│   │       ├── event/         # Event handling
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── validation/    # Validation logic
│   │       ├── helper/        # Helper utilities
│   │       ├── util/          # General utilities
│   │       ├── exception/     # Custom exceptions
│   │       ├── animation/     # Animation utilities
│   │       ├── observer/      # Observer pattern implementations
│   │       ├── plugin/        # Plugin architecture
│   │       └── startup/       # Startup initialization
│   │
│   └── resources
│
└── test
    └── java
        └── com.gallery
```

## Architecture Principles

### MVC Architecture
- **Model**: Domain objects representing images, folders, metadata
- **View**: JavaFX UI components (FXML or programmatic)
- **Controller**: Handles user input, coordinates between Model and View

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Subtypes must be substitutable for base types
- **Interface Segregation**: Many specific interfaces over one general interface
- **Dependency Inversion**: Depend on abstractions, not concretions

### Clean Architecture
- Separation of concerns across layers
- Business logic independent of UI framework
- Testable without dependencies on external systems

### Performance Design
- **Virtualized Rendering**: Only render visible items in grid
- **Lazy Loading**: Load images on-demand
- **Background Threads**: ExecutorService for non-blocking operations
- **Multi-level Caching**: Memory cache + Disk cache
- **Thumbnail Preloading**: Predictive loading based on scroll direction

## Building

```bash
# Compile the project
mvn clean compile

# Run the application
mvn javafx:run

# Run tests
mvn test

# Package the application
mvn package
```

## Requirements

- Java 21 LTS or higher
- Maven 3.8+
- Compatible OS: Windows, Linux, macOS

## License

MIT License
