# Orchestra Persistence Module

This module provides persistence capabilities for the Orchestra task management system, allowing Symphony, Choir, and Voice objects to be stored and retrieved in a JSON format.

## Architecture

The persistence module follows a layered architecture with clear separation of concerns:

```
┌─────────────────┐
│  Service Layer  │ ← High-level business operations
├─────────────────┤
│ Repository Layer│ ← Data access operations
├─────────────────┤
│   Mapper Layer  │ ← Object mapping between domain and DTOs
├─────────────────┤
│     DTO Layer   │ ← Data transfer objects for serialization
└─────────────────┘
```

### Key Design Principles

- **Separation of Concerns**: Each layer has a specific responsibility
- **Interface-based Design**: All components are defined by interfaces
- **Dependency Injection**: Components are wired together using Guice
- **Immutability**: Domain objects are immutable to ensure thread safety
- **Testability**: Components can be easily tested in isolation

## Components

### DTOs (Data Transfer Objects)

DTOs are used to transfer data between the domain model and the persistence layer. They are designed for JSON serialization/deserialization.

- `VoiceDTO`: Represents a Voice entity for persistence
- `ChoirDTO`: Represents a Choir entity for persistence
- `SymphonyDTO`: Represents a Symphony entity for persistence

### Mappers

Mappers convert between domain models and DTOs.

- `Mapper<D, T>`: Generic interface for mapping between domain models and DTOs
- `VoiceMapper`: Maps between Voice and VoiceDTO
- `ChoirMapper`: Maps between Choir and ChoirDTO
- `SymphonyMapper`: Maps between Symphony and SymphonyDTO

### Repositories

Repositories handle the storage and retrieval of domain objects.

- `Repository<T, ID>`: Generic interface for CRUD operations
- `SymphonyRepository`: Repository for Symphony objects
- `JsonSymphonyRepository`: JSON-based implementation of SymphonyRepository

### Services

Services provide high-level business operations on domain objects.

- `SymphonyService`: Service for Symphony operations
- `SymphonyServiceImpl`: Implementation of SymphonyService

## Usage

### Dependency Injection

The persistence components are configured in the `OrchestraModule` class and can be injected into other components:

```java
public class MyComponent {
    private final SymphonyService symphonyService;

    @Inject
    public MyComponent(SymphonyService symphonyService) {
        this.symphonyService = symphonyService;
    }
}
```

### Creating and Retrieving Symphonies

```java
// Create a new Symphony
Symphony symphony = symphonyService.createSymphony("My Symphony", "A description");

// Retrieve a Symphony by title
Optional<Symphony> optionalSymphony = symphonyService.getSymphony("My Symphony");
```

### Working with Choirs and Voices

```java
// Add a Choir to a Symphony
Choir choir = new Choir("My Choir", "A description", Collections.emptyList());
Symphony updatedSymphony = symphonyService.addChoir("My Symphony", choir);

// Add a Voice to a Choir
Voice voice = new Voice("My Voice", "A description", Collections.emptyList());
Symphony updatedSymphony = symphonyService.addVoice("My Symphony", "My Choir", voice);
```

## Storage

By default, Symphony objects are stored as JSON files in the `data/symphonies` directory. Each Symphony is stored in a separate file named after its title (with special characters replaced by underscores).

## Extension Points

The persistence module is designed to be extensible:

- **Alternative Storage**: Implement a different `SymphonyRepository` to use a different storage mechanism (e.g., database)
- **Additional Entities**: Add new DTOs, mappers, repositories, and services for additional entity types
- **Validation**: Add validation logic to the service layer
- **Caching**: Add caching to improve performance

## Dependencies

The persistence module depends on the following libraries:

- **Jackson**: For JSON serialization/deserialization
- **Guice**: For dependency injection
- **MapStruct**: For object mapping (optional, currently using manual mapping)