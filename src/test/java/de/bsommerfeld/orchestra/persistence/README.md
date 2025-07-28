# Orchestra Persistence Module Tests

This directory contains JUnit 5 (Jupiter) tests for the Orchestra persistence module. The tests cover all the main components of the persistence module:

## Test Structure

### Repository Layer Tests
- `JsonSymphonyRepositoryTest`: Tests the JSON-based implementation of the `SymphonyRepository` interface.
  - Tests CRUD operations (save, findById, findAll, deleteById, existsById)
  - Uses Mockito to mock the `SymphonyMapper` dependency

### Service Layer Tests
- `SymphonyServiceImplTest`: Tests the implementation of the `SymphonyService` interface.
  - Tests all service methods (createSymphony, getSymphony, getAllSymphonies, updateSymphony, deleteSymphony, addChoir, removeChoir, addVoice, removeVoice)
  - Uses Mockito to mock the `SymphonyRepository` dependency
  - Tests both success and failure scenarios

### Mapper Layer Tests
- `SymphonyMapperImplTest`: Tests the implementation of the `SymphonyMapper` interface.
  - Tests conversion between Symphony domain objects and SymphonyDTO objects
  - Uses Mockito to mock the `ChoirMapper` dependency
  - Tests edge cases like null inputs

### DTO Layer Tests
- `SymphonyDTOTest`: Tests the `SymphonyDTO` class.
  - Tests constructors, getters, setters, equals, hashCode, and toString methods
  - Tests edge cases like null values

## Running the Tests

To run the tests, you need to have JUnit 5 (Jupiter) and Mockito dependencies properly configured in your Maven project. The following dependencies should be added to your `pom.xml` file:

```xml
<!-- JUnit 5 (Jupiter) -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>4.11.0</version>
    <scope>test</scope>
</dependency>
```

Once the dependencies are properly configured, you can run the tests using one of the following methods:

### Using Maven

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=JsonSymphonyRepositoryTest

# Run a specific test method
mvn test -Dtest=JsonSymphonyRepositoryTest#save_shouldSaveSymphonyToFile
```

### Using IntelliJ IDEA

1. Right-click on the test class or method you want to run
2. Select "Run" or "Debug"

### Using Eclipse

1. Right-click on the test class or method you want to run
2. Select "Run As" > "JUnit Test"

## Test Coverage

The tests cover the following aspects of the persistence module:

- **Repository Layer**: Tests the CRUD operations for storing and retrieving Symphony objects as JSON files.
- **Service Layer**: Tests the business logic for managing Symphony objects, including adding and removing Choirs and Voices.
- **Mapper Layer**: Tests the conversion between domain objects and DTOs.
- **DTO Layer**: Tests the data transfer objects used for JSON serialization/deserialization.

## Troubleshooting

If you encounter issues running the tests, check the following:

1. Make sure the JUnit 5 (Jupiter) and Mockito dependencies are properly configured in your `pom.xml` file.
2. Make sure the test classes are in the correct package structure.
3. Make sure the test classes have the correct imports.
4. Make sure the test methods are properly annotated with `@Test`.