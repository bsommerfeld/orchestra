package de.bsommerfeld.orchestra.persistence.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for the Symphony entity.
 * This class is designed for JSON serialization/deserialization and data transfer between layers.
 */
public class SymphonyDTO {

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private List<ChoirDTO> choirs;

    /**
     * Default constructor for Jackson deserialization.
     */
    public SymphonyDTO() {
        // Required for Jackson
    }

    /**
     * Constructs a new SymphonyDTO with the specified properties.
     *
     * @param title The title of the symphony/project
     * @param description The description of the symphony/project (can be null)
     * @param createdAt The creation timestamp
     * @param choirs The list of choirs/task lists (can be null)
     */
    public SymphonyDTO(String title, String description, LocalDateTime createdAt, List<ChoirDTO> choirs) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.choirs = choirs != null ? new ArrayList<>(choirs) : null;
    }

    /**
     * Gets the title of this symphony/project.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this symphony/project.
     *
     * @param title The title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of this symphony/project.
     *
     * @return The description (can be null)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this symphony/project.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the creation timestamp of this symphony/project.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of this symphony/project.
     *
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the list of choirs/task lists of this symphony/project.
     *
     * @return The list of choirs (can be null)
     */
    public List<ChoirDTO> getChoirs() {
        return choirs;
    }

    /**
     * Sets the list of choirs/task lists of this symphony/project.
     *
     * @param choirs The list of choirs to set
     */
    public void setChoirs(List<ChoirDTO> choirs) {
        this.choirs = choirs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymphonyDTO that = (SymphonyDTO) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(choirs, that.choirs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, createdAt, choirs);
    }

    @Override
    public String toString() {
        return "SymphonyDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", choirs=" + (choirs != null ? choirs.size() : 0) + " items" +
                '}';
    }
}