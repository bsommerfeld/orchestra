package de.bsommerfeld.orchestra.persistence.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for the Voice entity.
 * This class is designed for JSON serialization/deserialization and data transfer between layers.
 */
public class VoiceDTO {

    private String title;
    private String description;
    private List<VoiceDTO> subVoices;

    /**
     * Default constructor for Jackson deserialization.
     */
    public VoiceDTO() {
        // Required for Jackson
    }

    /**
     * Constructs a new VoiceDTO with the specified properties.
     *
     * @param title The title of the voice/task
     * @param description The description of the voice/task (can be null)
     * @param subVoices The list of sub-voices (can be null)
     */
    public VoiceDTO(String title, String description, List<VoiceDTO> subVoices) {
        this.title = title;
        this.description = description;
        this.subVoices = subVoices != null ? new ArrayList<>(subVoices) : null;
    }

    /**
     * Gets the title of this voice/task.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this voice/task.
     *
     * @param title The title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of this voice/task.
     *
     * @return The description (can be null)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this voice/task.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list of sub-voices of this voice/task.
     *
     * @return The list of sub-voices (can be null)
     */
    public List<VoiceDTO> getSubVoices() {
        return subVoices;
    }

    /**
     * Sets the list of sub-voices of this voice/task.
     *
     * @param subVoices The list of sub-voices to set
     */
    public void setSubVoices(List<VoiceDTO> subVoices) {
        this.subVoices = subVoices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceDTO voiceDTO = (VoiceDTO) o;
        return Objects.equals(title, voiceDTO.title) &&
                Objects.equals(description, voiceDTO.description) &&
                Objects.equals(subVoices, voiceDTO.subVoices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, subVoices);
    }

    @Override
    public String toString() {
        return "VoiceDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", subVoices=" + (subVoices != null ? subVoices.size() : 0) + " items" +
                '}';
    }
}