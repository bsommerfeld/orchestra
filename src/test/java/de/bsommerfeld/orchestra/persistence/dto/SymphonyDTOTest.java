package de.bsommerfeld.orchestra.persistence.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SymphonyDTOTest {

    private static final String TEST_TITLE = "Test Symphony";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now();

    @Test
    void defaultConstructor_shouldCreateEmptyDTO() {
        // Act
        SymphonyDTO dto = new SymphonyDTO();

        // Assert
        assertNull(dto.getTitle());
        assertNull(dto.getDescription());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getChoirs());
    }

    @Test
    void parameterizedConstructor_shouldCreateDTOWithValues() {
        // Arrange
        List<ChoirDTO> choirs = Arrays.asList(
            new ChoirDTO("Choir 1", "Description 1", null),
            new ChoirDTO("Choir 2", "Description 2", null)
        );

        // Act
        SymphonyDTO dto = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, choirs);

        // Assert
        assertEquals(TEST_TITLE, dto.getTitle());
        assertEquals(TEST_DESCRIPTION, dto.getDescription());
        assertEquals(TEST_CREATED_AT, dto.getCreatedAt());
        assertEquals(2, dto.getChoirs().size());
        assertEquals("Choir 1", dto.getChoirs().get(0).getName());
        assertEquals("Choir 2", dto.getChoirs().get(1).getName());
    }

    @Test
    void parameterizedConstructor_shouldHandleNullChoirs() {
        // Act
        SymphonyDTO dto = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, null);

        // Assert
        assertEquals(TEST_TITLE, dto.getTitle());
        assertEquals(TEST_DESCRIPTION, dto.getDescription());
        assertEquals(TEST_CREATED_AT, dto.getCreatedAt());
        assertNull(dto.getChoirs());
    }

    @Test
    void setTitle_shouldUpdateTitle() {
        // Arrange
        SymphonyDTO dto = new SymphonyDTO();

        // Act
        dto.setTitle(TEST_TITLE);

        // Assert
        assertEquals(TEST_TITLE, dto.getTitle());
    }

    @Test
    void setDescription_shouldUpdateDescription() {
        // Arrange
        SymphonyDTO dto = new SymphonyDTO();

        // Act
        dto.setDescription(TEST_DESCRIPTION);

        // Assert
        assertEquals(TEST_DESCRIPTION, dto.getDescription());
    }

    @Test
    void setCreatedAt_shouldUpdateCreatedAt() {
        // Arrange
        SymphonyDTO dto = new SymphonyDTO();

        // Act
        dto.setCreatedAt(TEST_CREATED_AT);

        // Assert
        assertEquals(TEST_CREATED_AT, dto.getCreatedAt());
    }

    @Test
    void setChoirs_shouldUpdateChoirs() {
        // Arrange
        SymphonyDTO dto = new SymphonyDTO();
        List<ChoirDTO> choirs = Arrays.asList(
            new ChoirDTO("Choir 1", "Description 1", null),
            new ChoirDTO("Choir 2", "Description 2", null)
        );

        // Act
        dto.setChoirs(choirs);

        // Assert
        assertEquals(2, dto.getChoirs().size());
        assertEquals("Choir 1", dto.getChoirs().get(0).getName());
        assertEquals("Choir 2", dto.getChoirs().get(1).getName());
    }

    @Test
    void equals_shouldReturnTrueForEqualDTOs() {
        // Arrange
        List<ChoirDTO> choirs = Arrays.asList(
            new ChoirDTO("Choir 1", "Description 1", null)
        );
        SymphonyDTO dto1 = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, choirs);
        SymphonyDTO dto2 = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, choirs);

        // Act & Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentDTOs() {
        // Arrange
        SymphonyDTO dto1 = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, null);
        SymphonyDTO dto2 = new SymphonyDTO("Different Title", TEST_DESCRIPTION, TEST_CREATED_AT, null);

        // Act & Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_shouldReturnStringRepresentation() {
        // Arrange
        List<ChoirDTO> choirs = Arrays.asList(
            new ChoirDTO("Choir 1", "Description 1", null),
            new ChoirDTO("Choir 2", "Description 2", null)
        );
        SymphonyDTO dto = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, choirs);

        // Act
        String result = dto.toString();

        // Assert
        assertTrue(result.contains(TEST_TITLE));
        assertTrue(result.contains(TEST_DESCRIPTION));
        assertTrue(result.contains(TEST_CREATED_AT.toString()));
        assertTrue(result.contains("2 items"));
    }

    @Test
    void toString_shouldHandleNullChoirs() {
        // Arrange
        SymphonyDTO dto = new SymphonyDTO(TEST_TITLE, TEST_DESCRIPTION, TEST_CREATED_AT, null);

        // Act
        String result = dto.toString();

        // Assert
        assertTrue(result.contains(TEST_TITLE));
        assertTrue(result.contains(TEST_DESCRIPTION));
        assertTrue(result.contains(TEST_CREATED_AT.toString()));
        assertTrue(result.contains("0 items"));
    }
}