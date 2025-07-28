package de.bsommerfeld.orchestra.persistence.mapper;

import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.dto.ChoirDTO;
import de.bsommerfeld.orchestra.persistence.dto.SymphonyDTO;
import de.bsommerfeld.orchestra.persistence.dto.VoiceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SymphonyMapperImplTest {

    private static final String TEST_TITLE = "Test Symphony";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_CHOIR_NAME = "Test Choir";
    private static final String TEST_VOICE_TITLE = "Test Voice";

    @Mock
    private ChoirMapper choirMapper;

    private SymphonyMapperImpl symphonyMapper;
    private Symphony testSymphony;
    private SymphonyDTO testSymphonyDTO;
    private List<Choir> testChoirs;
    private List<ChoirDTO> testChoirDTOs;

    @BeforeEach
    void setUp() {
        // Initialize the mapper with the mock choir mapper
        symphonyMapper = new SymphonyMapperImpl(choirMapper);

        // Create test objects
        Voice testVoice = new Voice(TEST_VOICE_TITLE, "Test Voice Description", Collections.emptyList());
        Choir testChoir = new Choir(TEST_CHOIR_NAME, "Test Choir Description", Collections.singletonList(testVoice));
        testChoirs = Collections.singletonList(testChoir);
        testSymphony = new Symphony(TEST_TITLE, TEST_DESCRIPTION, testChoirs);

        // Create test DTOs
        VoiceDTO testVoiceDTO = new VoiceDTO(TEST_VOICE_TITLE, "Test Voice Description", Collections.emptyList());
        ChoirDTO testChoirDTO = new ChoirDTO(TEST_CHOIR_NAME, "Test Choir Description", Collections.singletonList(testVoiceDTO));
        testChoirDTOs = Collections.singletonList(testChoirDTO);
        testSymphonyDTO = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, testSymphony.getCreatedAt(), testChoirDTOs);
    }

    @Test
    void toDto_shouldConvertSymphonyToSymphonyDTO() {
        // Arrange
        when(choirMapper.toDtoList(testChoirs)).thenReturn(testChoirDTOs);

        // Act
        SymphonyDTO result = symphonyMapper.toDto(testSymphony);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(TEST_DESCRIPTION, result.getDescription());
        assertEquals(testSymphony.getCreatedAt(), result.getCreatedAt());
        assertEquals(testChoirDTOs, result.getChoirs());
        verify(choirMapper).toDtoList(testChoirs);
    }

    @Test
    void toDto_shouldReturnNullWhenSymphonyIsNull() {
        // Act
        SymphonyDTO result = symphonyMapper.toDto(null);

        // Assert
        assertNull(result);
        verify(choirMapper, never()).toDtoList(anyList());
    }

    @Test
    void toDomain_shouldConvertSymphonyDTOToSymphony() {
        // Arrange
        when(choirMapper.toDomainList(testChoirDTOs)).thenReturn(testChoirs);

        // Act
        Symphony result = symphonyMapper.toDomain(testSymphonyDTO);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(TEST_DESCRIPTION, result.getDescription().orElse(null));
        assertEquals(testChoirs, result.getChoirs());
        verify(choirMapper).toDomainList(testChoirDTOs);
    }

    @Test
    void toDomain_shouldReturnNullWhenSymphonyDTOIsNull() {
        // Act
        Symphony result = symphonyMapper.toDomain(null);

        // Assert
        assertNull(result);
        verify(choirMapper, never()).toDomainList(anyList());
    }
}