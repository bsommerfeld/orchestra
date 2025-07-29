package de.bsommerfeld.orchestra.persistence.repository;

import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.dto.SymphonyDTO;
import de.bsommerfeld.orchestra.persistence.mapper.SymphonyMapper;
import de.bsommerfeld.orchestra.persistence.path.PlatformPathProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonSymphonyRepositoryTest {

    private static final String TEST_STORAGE_DIR = "data/test-symphonies";
    private static final String TEST_TITLE = "Test Symphony";
    private static final String TEST_DESCRIPTION = "Test Description";

    @Mock
    private SymphonyMapper symphonyMapper;
    
    @Mock
    private PlatformPathProvider pathProvider;

    private TestJsonSymphonyRepository repository;
    private Symphony testSymphony;
    private SymphonyDTO testSymphonyDTO;
    
    /**
     * Test subclass of JsonSymphonyRepository that overrides the storage directory.
     */
    private static class TestJsonSymphonyRepository extends JsonSymphonyRepository {
        public TestJsonSymphonyRepository(SymphonyMapper symphonyMapper, PlatformPathProvider pathProvider) {
            super(symphonyMapper, pathProvider);
        }
        
        @Override
        protected String getStorageDir() {
            return TEST_STORAGE_DIR;
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        // Create test directory
        Path testDir = Paths.get(TEST_STORAGE_DIR);
        if (Files.exists(testDir)) {
            // Clean up any existing test files
            Files.walk(testDir)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } else {
            Files.createDirectories(testDir);
        }

        // Create test objects
        testSymphony = new Symphony(TEST_TITLE, TEST_DESCRIPTION, null);
        testSymphonyDTO = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, testSymphony.getCreatedAt(), null);

        // Configure mocks with lenient stubbings to avoid unnecessary stubbing warnings
        Mockito.lenient().when(symphonyMapper.toDto(any(Symphony.class))).thenReturn(testSymphonyDTO);
        Mockito.lenient().when(symphonyMapper.toDomain(any(SymphonyDTO.class))).thenReturn(testSymphony);
        
        // Configure path provider mock
        Path legacyPath = Paths.get(TEST_STORAGE_DIR);
        Mockito.lenient().when(pathProvider.getLegacyStorageDirectory()).thenReturn(legacyPath);
        Mockito.lenient().when(pathProvider.getSymphonyDirectory()).thenReturn(legacyPath);

        // Create repository with the symphony mapper and path provider
        repository = new TestJsonSymphonyRepository(symphonyMapper, pathProvider);
    }

    @Test
    void save_shouldSaveSymphonyToFile() {
        // Act
        Symphony result = repository.save(testSymphony);

        // Assert
        assertEquals(testSymphony, result);
        verify(symphonyMapper).toDto(testSymphony);
        assertTrue(Files.exists(Paths.get(TEST_STORAGE_DIR, TEST_TITLE.replaceAll("[^a-zA-Z0-9.-]", "_") + ".json")));
    }

    @Test
    void save_shouldThrowExceptionWhenSymphonyIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void findById_shouldReturnSymphonyWhenExists() {
        // Arrange
        repository.save(testSymphony);

        // Act
        Optional<Symphony> result = repository.findById(TEST_TITLE);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSymphony, result.get());
        verify(symphonyMapper).toDomain(any(SymphonyDTO.class));
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenDoesNotExist() {
        // Act
        Optional<Symphony> result = repository.findById("NonExistentSymphony");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenIdIsNull() {
        // Act
        Optional<Symphony> result = repository.findById(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_shouldReturnAllSymphonies() {
        // Arrange
        // First, make sure the test directory is empty
        try {
            Path testDir = Paths.get(TEST_STORAGE_DIR);
            if (Files.exists(testDir)) {
                Files.walk(testDir)
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            fail("Failed to clean up test directory: " + e.getMessage());
        }
        
        Symphony symphony1 = new Symphony("Symphony 1", "Description 1", null);
        Symphony symphony2 = new Symphony("Symphony 2", "Description 2", null);
        
        SymphonyDTO dto1 = new SymphonyDTO("Symphony 1", "Description 1", symphony1.getCreatedAt(), null);
        SymphonyDTO dto2 = new SymphonyDTO("Symphony 2", "Description 2", symphony2.getCreatedAt(), null);
        
        // Use lenient stubbings to avoid unnecessary stubbing warnings
        Mockito.lenient().when(symphonyMapper.toDto(symphony1)).thenReturn(dto1);
        Mockito.lenient().when(symphonyMapper.toDto(symphony2)).thenReturn(dto2);
        
        Mockito.lenient().when(symphonyMapper.toDomain(dto1)).thenReturn(symphony1);
        Mockito.lenient().when(symphonyMapper.toDomain(dto2)).thenReturn(symphony2);
        
        repository.save(symphony1);
        repository.save(symphony2);

        // Act
        List<Symphony> result = repository.findAll();

        // Assert
        assertEquals(2, result.size(), "Expected exactly 2 symphonies, but found " + result.size());
        assertTrue(result.contains(symphony1), "Result should contain symphony1");
        assertTrue(result.contains(symphony2), "Result should contain symphony2");
    }

    @Test
    void deleteById_shouldDeleteSymphonyWhenExists() {
        // Arrange
        repository.save(testSymphony);

        // Act
        boolean result = repository.deleteById(TEST_TITLE);

        // Assert
        assertTrue(result);
        assertFalse(Files.exists(Paths.get(TEST_STORAGE_DIR, TEST_TITLE.replaceAll("[^a-zA-Z0-9.-]", "_") + ".json")));
    }

    @Test
    void deleteById_shouldReturnFalseWhenDoesNotExist() {
        // Act
        boolean result = repository.deleteById("NonExistentSymphony");

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteById_shouldReturnFalseWhenIdIsNull() {
        // Act
        boolean result = repository.deleteById(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void existsById_shouldReturnTrueWhenExists() {
        // Arrange
        repository.save(testSymphony);

        // Act
        boolean result = repository.existsById(TEST_TITLE);

        // Assert
        assertTrue(result);
    }

    @Test
    void existsById_shouldReturnFalseWhenDoesNotExist() {
        // Act
        boolean result = repository.existsById("NonExistentSymphony");

        // Assert
        assertFalse(result);
    }

    @Test
    void existsById_shouldReturnFalseWhenIdIsNull() {
        // Act
        boolean result = repository.existsById(null);

        // Assert
        assertFalse(result);
    }
}