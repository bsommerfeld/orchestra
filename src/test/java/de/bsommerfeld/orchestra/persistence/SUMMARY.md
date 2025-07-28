# Orchestra Persistence Module Tests Summary

## Overview

This document provides a summary of the JUnit 5 (Jupiter) and Mockito tests implemented for the Orchestra persistence module. The tests cover all the main components of the persistence module, including the repository, service, mapper, and DTO layers.

## Test Classes

### Repository Layer

#### JsonSymphonyRepositoryTest

Tests the JSON-based implementation of the `SymphonyRepository` interface.

**Test Methods:**
- `save_shouldSaveSymphonyToFile`: Verifies that a Symphony is correctly saved to a JSON file.
- `save_shouldThrowExceptionWhenSymphonyIsNull`: Verifies that an exception is thrown when trying to save a null Symphony.
- `findById_shouldReturnSymphonyWhenExists`: Verifies that a Symphony can be retrieved by its ID (title) when it exists.
- `findById_shouldReturnEmptyOptionalWhenDoesNotExist`: Verifies that an empty Optional is returned when trying to retrieve a non-existent Symphony.
- `findById_shouldReturnEmptyOptionalWhenIdIsNull`: Verifies that an empty Optional is returned when the ID is null.
- `findAll_shouldReturnAllSymphonies`: Verifies that all Symphonies can be retrieved.
- `deleteById_shouldDeleteSymphonyWhenExists`: Verifies that a Symphony is correctly deleted by its ID (title).
- `deleteById_shouldReturnFalseWhenDoesNotExist`: Verifies that false is returned when trying to delete a non-existent Symphony.
- `deleteById_shouldReturnFalseWhenIdIsNull`: Verifies that false is returned when the ID is null.
- `existsById_shouldReturnTrueWhenExists`: Verifies that true is returned when checking if a Symphony exists by its ID (title).
- `existsById_shouldReturnFalseWhenDoesNotExist`: Verifies that false is returned when checking if a non-existent Symphony exists.
- `existsById_shouldReturnFalseWhenIdIsNull`: Verifies that false is returned when the ID is null.

### Service Layer

#### SymphonyServiceImplTest

Tests the implementation of the `SymphonyService` interface.

**Test Methods:**
- `createSymphony_shouldCreateAndSaveSymphony`: Verifies that a Symphony is correctly created and saved.
- `createSymphony_shouldThrowExceptionWhenSymphonyExists`: Verifies that an exception is thrown when trying to create a Symphony with a title that already exists.
- `getSymphony_shouldReturnSymphonyWhenExists`: Verifies that a Symphony can be retrieved by its title when it exists.
- `getSymphony_shouldReturnEmptyOptionalWhenDoesNotExist`: Verifies that an empty Optional is returned when trying to retrieve a non-existent Symphony.
- `getAllSymphonies_shouldReturnAllSymphonies`: Verifies that all Symphonies can be retrieved.
- `updateSymphony_shouldUpdateSymphonyWhenExists`: Verifies that a Symphony is correctly updated when it exists.
- `updateSymphony_shouldThrowExceptionWhenDoesNotExist`: Verifies that an exception is thrown when trying to update a non-existent Symphony.
- `deleteSymphony_shouldDeleteSymphonyWhenExists`: Verifies that a Symphony is correctly deleted by its title.
- `deleteSymphony_shouldReturnFalseWhenDoesNotExist`: Verifies that false is returned when trying to delete a non-existent Symphony.
- `addChoir_shouldAddChoirToSymphonyWhenExists`: Verifies that a Choir is correctly added to a Symphony when the Symphony exists.
- `addChoir_shouldThrowExceptionWhenSymphonyDoesNotExist`: Verifies that an exception is thrown when trying to add a Choir to a non-existent Symphony.
- `removeChoir_shouldRemoveChoirFromSymphonyWhenExists`: Verifies that a Choir is correctly removed from a Symphony when both the Symphony and Choir exist.
- `removeChoir_shouldThrowExceptionWhenSymphonyDoesNotExist`: Verifies that an exception is thrown when trying to remove a Choir from a non-existent Symphony.
- `removeChoir_shouldThrowExceptionWhenChoirDoesNotExist`: Verifies that an exception is thrown when trying to remove a non-existent Choir from a Symphony.
- `addVoice_shouldAddVoiceToChoirWhenExists`: Verifies that a Voice is correctly added to a Choir in a Symphony when both the Symphony and Choir exist.
- `addVoice_shouldThrowExceptionWhenSymphonyDoesNotExist`: Verifies that an exception is thrown when trying to add a Voice to a Choir in a non-existent Symphony.
- `addVoice_shouldThrowExceptionWhenChoirDoesNotExist`: Verifies that an exception is thrown when trying to add a Voice to a non-existent Choir in a Symphony.
- `removeVoice_shouldRemoveVoiceFromChoirWhenExists`: Verifies that a Voice is correctly removed from a Choir in a Symphony when all three exist.
- `removeVoice_shouldThrowExceptionWhenSymphonyDoesNotExist`: Verifies that an exception is thrown when trying to remove a Voice from a Choir in a non-existent Symphony.
- `removeVoice_shouldThrowExceptionWhenChoirDoesNotExist`: Verifies that an exception is thrown when trying to remove a Voice from a non-existent Choir in a Symphony.
- `removeVoice_shouldThrowExceptionWhenVoiceDoesNotExist`: Verifies that an exception is thrown when trying to remove a non-existent Voice from a Choir in a Symphony.

### Mapper Layer

#### SymphonyMapperImplTest

Tests the implementation of the `SymphonyMapper` interface.

**Test Methods:**
- `toDto_shouldConvertSymphonyToSymphonyDTO`: Verifies that a Symphony domain object is correctly converted to a SymphonyDTO.
- `toDto_shouldReturnNullWhenSymphonyIsNull`: Verifies that null is returned when trying to convert a null Symphony.
- `toDomain_shouldConvertSymphonyDTOToSymphony`: Verifies that a SymphonyDTO is correctly converted to a Symphony domain object.
- `toDomain_shouldReturnNullWhenSymphonyDTOIsNull`: Verifies that null is returned when trying to convert a null SymphonyDTO.

### DTO Layer

#### SymphonyDTOTest

Tests the `SymphonyDTO` class.

**Test Methods:**
- `defaultConstructor_shouldCreateEmptyDTO`: Verifies that the default constructor creates an empty DTO.
- `parameterizedConstructor_shouldCreateDTOWithValues`: Verifies that the parameterized constructor correctly initializes the DTO with the provided values.
- `parameterizedConstructor_shouldHandleNullChoirs`: Verifies that the parameterized constructor correctly handles null choirs.
- `setTitle_shouldUpdateTitle`: Verifies that the title can be updated.
- `setDescription_shouldUpdateDescription`: Verifies that the description can be updated.
- `setCreatedAt_shouldUpdateCreatedAt`: Verifies that the creation timestamp can be updated.
- `setChoirs_shouldUpdateChoirs`: Verifies that the list of choirs can be updated.
- `equals_shouldReturnTrueForEqualDTOs`: Verifies that the equals method returns true for equal DTOs.
- `equals_shouldReturnFalseForDifferentDTOs`: Verifies that the equals method returns false for different DTOs.
- `toString_shouldReturnStringRepresentation`: Verifies that the toString method returns a string representation of the DTO.
- `toString_shouldHandleNullChoirs`: Verifies that the toString method correctly handles null choirs.

## Test Coverage

The tests provide comprehensive coverage of the persistence module:

- **Repository Layer**: Tests all CRUD operations for storing and retrieving Symphony objects as JSON files.
- **Service Layer**: Tests all business logic for managing Symphony objects, including adding and removing Choirs and Voices.
- **Mapper Layer**: Tests the conversion between domain objects and DTOs, including edge cases like null inputs.
- **DTO Layer**: Tests all aspects of the DTO classes, including constructors, getters, setters, equals, hashCode, and toString methods.

## Conclusion

The implemented tests ensure that the Orchestra persistence module functions correctly and handles edge cases appropriately. Once the dependency issues are resolved, these tests will provide a solid foundation for maintaining the quality of the persistence module.