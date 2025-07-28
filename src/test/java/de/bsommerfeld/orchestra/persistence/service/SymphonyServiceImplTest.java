package de.bsommerfeld.orchestra.persistence.service;

import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.repository.SymphonyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SymphonyServiceImplTest {

    private static final String TEST_TITLE = "Test Symphony";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_CHOIR_NAME = "Test Choir";
    private static final String TEST_VOICE_TITLE = "Test Voice";

    @Mock
    private SymphonyRepository symphonyRepository;

    private SymphonyServiceImpl symphonyService;
    private Symphony testSymphony;
    private Choir testChoir;
    private Voice testVoice;

    @BeforeEach
    void setUp() {
        // Initialize the service with the mock repository
        symphonyService = new SymphonyServiceImpl(symphonyRepository);

        // Create test objects
        testVoice = new Voice(TEST_VOICE_TITLE, "Test Voice Description", Collections.emptyList());
        testChoir = new Choir(TEST_CHOIR_NAME, "Test Choir Description", Collections.singletonList(testVoice));
        testSymphony = new Symphony(TEST_TITLE, TEST_DESCRIPTION, Collections.singletonList(testChoir));
    }

    @Test
    void createSymphony_shouldCreateAndSaveSymphony() {
        // Arrange
        when(symphonyRepository.existsById(TEST_TITLE)).thenReturn(false);
        when(symphonyRepository.save(any(Symphony.class))).thenReturn(testSymphony);

        // Act
        Symphony result = symphonyService.createSymphony(TEST_TITLE, TEST_DESCRIPTION);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(TEST_DESCRIPTION, result.getDescription().orElse(null));
        verify(symphonyRepository).existsById(TEST_TITLE);
        verify(symphonyRepository).save(any(Symphony.class));
    }

    @Test
    void createSymphony_shouldThrowExceptionWhenSymphonyExists() {
        // Arrange
        when(symphonyRepository.existsById(TEST_TITLE)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> symphonyService.createSymphony(TEST_TITLE, TEST_DESCRIPTION));
        verify(symphonyRepository).existsById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void getSymphony_shouldReturnSymphonyWhenExists() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));

        // Act
        Optional<Symphony> result = symphonyService.getSymphony(TEST_TITLE);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSymphony, result.get());
        verify(symphonyRepository).findById(TEST_TITLE);
    }

    @Test
    void getSymphony_shouldReturnEmptyOptionalWhenDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.empty());

        // Act
        Optional<Symphony> result = symphonyService.getSymphony(TEST_TITLE);

        // Assert
        assertFalse(result.isPresent());
        verify(symphonyRepository).findById(TEST_TITLE);
    }

    @Test
    void getAllSymphonies_shouldReturnAllSymphonies() {
        // Arrange
        List<Symphony> symphonies = Collections.singletonList(testSymphony);
        when(symphonyRepository.findAll()).thenReturn(symphonies);

        // Act
        List<Symphony> result = symphonyService.getAllSymphonies();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testSymphony, result.get(0));
        verify(symphonyRepository).findAll();
    }

    @Test
    void updateSymphony_shouldUpdateSymphonyWhenExists() {
        // Arrange
        when(symphonyRepository.existsById(TEST_TITLE)).thenReturn(true);
        when(symphonyRepository.save(testSymphony)).thenReturn(testSymphony);

        // Act
        Symphony result = symphonyService.updateSymphony(testSymphony);

        // Assert
        assertEquals(testSymphony, result);
        verify(symphonyRepository).existsById(TEST_TITLE);
        verify(symphonyRepository).save(testSymphony);
    }

    @Test
    void updateSymphony_shouldThrowExceptionWhenDoesNotExist() {
        // Arrange
        when(symphonyRepository.existsById(TEST_TITLE)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> symphonyService.updateSymphony(testSymphony));
        verify(symphonyRepository).existsById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void deleteSymphony_shouldDeleteSymphonyWhenExists() {
        // Arrange
        when(symphonyRepository.deleteById(TEST_TITLE)).thenReturn(true);

        // Act
        boolean result = symphonyService.deleteSymphony(TEST_TITLE);

        // Assert
        assertTrue(result);
        verify(symphonyRepository).deleteById(TEST_TITLE);
    }

    @Test
    void deleteSymphony_shouldReturnFalseWhenDoesNotExist() {
        // Arrange
        when(symphonyRepository.deleteById(TEST_TITLE)).thenReturn(false);

        // Act
        boolean result = symphonyService.deleteSymphony(TEST_TITLE);

        // Assert
        assertFalse(result);
        verify(symphonyRepository).deleteById(TEST_TITLE);
    }

    @Test
    void addChoir_shouldAddChoirToSymphonyWhenExists() {
        // Arrange
        Symphony symphonyWithoutChoir = new Symphony(TEST_TITLE, TEST_DESCRIPTION, Collections.emptyList());
        Choir newChoir = new Choir("New Choir", "New Choir Description", Collections.emptyList());
        
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(symphonyWithoutChoir));
        when(symphonyRepository.save(any(Symphony.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Symphony result = symphonyService.addChoir(TEST_TITLE, newChoir);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getChoirs().size());
        assertEquals("New Choir", result.getChoirs().get(0).getName());
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository).save(any(Symphony.class));
    }

    @Test
    void addChoir_shouldThrowExceptionWhenSymphonyDoesNotExist() {
        // Arrange
        Choir newChoir = new Choir("New Choir", "New Choir Description", Collections.emptyList());
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> symphonyService.addChoir(TEST_TITLE, newChoir));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void removeChoir_shouldRemoveChoirFromSymphonyWhenExists() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));
        when(symphonyRepository.save(any(Symphony.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Symphony result = symphonyService.removeChoir(TEST_TITLE, TEST_CHOIR_NAME);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getChoirs().size());
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository).save(any(Symphony.class));
    }

    @Test
    void removeChoir_shouldThrowExceptionWhenSymphonyDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> symphonyService.removeChoir(TEST_TITLE, TEST_CHOIR_NAME));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void removeChoir_shouldThrowExceptionWhenChoirDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> symphonyService.removeChoir(TEST_TITLE, "Non-existent Choir"));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void addVoice_shouldAddVoiceToChoirWhenExists() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));
        when(symphonyRepository.save(any(Symphony.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Voice newVoice = new Voice("New Voice", "New Voice Description", Collections.emptyList());

        // Act
        Symphony result = symphonyService.addVoice(TEST_TITLE, TEST_CHOIR_NAME, newVoice);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getChoirs().size());
        assertEquals(2, result.getChoirs().get(0).getVoices().size());
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository).save(any(Symphony.class));
    }

    @Test
    void addVoice_shouldThrowExceptionWhenSymphonyDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.empty());
        
        Voice newVoice = new Voice("New Voice", "New Voice Description", Collections.emptyList());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            symphonyService.addVoice(TEST_TITLE, TEST_CHOIR_NAME, newVoice));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void addVoice_shouldThrowExceptionWhenChoirDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));
        
        Voice newVoice = new Voice("New Voice", "New Voice Description", Collections.emptyList());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            symphonyService.addVoice(TEST_TITLE, "Non-existent Choir", newVoice));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void removeVoice_shouldRemoveVoiceFromChoirWhenExists() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));
        when(symphonyRepository.save(any(Symphony.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Symphony result = symphonyService.removeVoice(TEST_TITLE, TEST_CHOIR_NAME, TEST_VOICE_TITLE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getChoirs().size());
        assertEquals(0, result.getChoirs().get(0).getVoices().size());
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository).save(any(Symphony.class));
    }

    @Test
    void removeVoice_shouldThrowExceptionWhenSymphonyDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            symphonyService.removeVoice(TEST_TITLE, TEST_CHOIR_NAME, TEST_VOICE_TITLE));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void removeVoice_shouldThrowExceptionWhenChoirDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            symphonyService.removeVoice(TEST_TITLE, "Non-existent Choir", TEST_VOICE_TITLE));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }

    @Test
    void removeVoice_shouldThrowExceptionWhenVoiceDoesNotExist() {
        // Arrange
        when(symphonyRepository.findById(TEST_TITLE)).thenReturn(Optional.of(testSymphony));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            symphonyService.removeVoice(TEST_TITLE, TEST_CHOIR_NAME, "Non-existent Voice"));
        verify(symphonyRepository).findById(TEST_TITLE);
        verify(symphonyRepository, never()).save(any(Symphony.class));
    }
}