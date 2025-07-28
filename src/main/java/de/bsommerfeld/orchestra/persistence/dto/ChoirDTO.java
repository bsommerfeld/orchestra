package de.bsommerfeld.orchestra.persistence.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for the Choir entity.
 * This class is designed for JSON serialization/deserialization and data transfer between layers.
 */
public class ChoirDTO {

    private String name;
    private String description;
    private List<VoiceDTO> voices;

    /**
     * Default constructor for Jackson deserialization.
     */
    public ChoirDTO() {
        // Required for Jackson
    }

    /**
     * Constructs a new ChoirDTO with the specified properties.
     *
     * @param name The name of the choir/task list
     * @param description The description of the choir/task list (can be null)
     * @param voices The list of voices/tasks (can be null)
     */
    public ChoirDTO(String name, String description, List<VoiceDTO> voices) {
        this.name = name;
        this.description = description;
        this.voices = voices != null ? new ArrayList<>(voices) : null;
    }

    /**
     * Gets the name of this choir/task list.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this choir/task list.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of this choir/task list.
     *
     * @return The description (can be null)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this choir/task list.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list of voices/tasks of this choir/task list.
     *
     * @return The list of voices (can be null)
     */
    public List<VoiceDTO> getVoices() {
        return voices;
    }

    /**
     * Sets the list of voices/tasks of this choir/task list.
     *
     * @param voices The list of voices to set
     */
    public void setVoices(List<VoiceDTO> voices) {
        this.voices = voices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChoirDTO choirDTO = (ChoirDTO) o;
        return Objects.equals(name, choirDTO.name) &&
                Objects.equals(description, choirDTO.description) &&
                Objects.equals(voices, choirDTO.voices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, voices);
    }

    @Override
    public String toString() {
        return "ChoirDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", voices=" + (voices != null ? voices.size() : 0) + " items" +
                '}';
    }
}