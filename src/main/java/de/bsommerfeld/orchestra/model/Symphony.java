package de.bsommerfeld.orchestra.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a "Symphony" in the Orchestra task management system, serving as the top-level project container.
 * A Symphony encompasses one or more "choirs" (task lists), each of which in turn contains "Voices" (individual tasks/sub-tasks).
 * It symbolizes the grand composition, bringing together various musical sections to form a complete and cohesive work.
 * Symphonies are immutable once created.
 */
public final class Symphony {

    private final String title;
    private final String description;
    private final LocalDateTime createdAt;
    private final List<Choir> choirs; // A Symphony contains multiple choirs

    /**
     * Constructs a new Symphony instance.
     *
     * @param title The mandatory title of the symphony/project. Cannot be null or empty.
     * @param description An optional description of the symphony/project. Can be null or empty.
     * @param choirs A list of choirs (task lists) belonging to this symphony. Can be null or empty.
     * The list will be defensively copied to ensure immutability.
     * @throws IllegalArgumentException if the title is null or empty.
     */
    public Symphony(String title, String description, List<Choir> choirs) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Symphony title cannot be null or empty.");
        }
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now(); // Automatically set creation timestamp
        this.choirs = (choirs != null) ? List.copyOf(choirs) : Collections.emptyList(); // Defensive copy
    }

    /**
     * Returns the title of this symphony/project.
     *
     * @return The title of the symphony.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the optional description of this symphony/project.
     *
     * @return An {@link Optional} containing the description, or an empty Optional if no description is present.
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * Returns the timestamp when this symphony was created.
     *
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns an immutable list of choirs (task lists) belonging to this symphony.
     *
     * @return An unmodifiable {@link List} of {@link Choir} objects. Returns an empty list if no choirs exist.
     */
    public List<Choir> getChoirs() {
        return choirs; // Already an immutable copy
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symphony symphony = (Symphony) o;
        return Objects.equals(title, symphony.title) &&
                Objects.equals(description, symphony.description) &&
                Objects.equals(createdAt, symphony.createdAt) &&
                Objects.equals(choirs, symphony.choirs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, createdAt, choirs);
    }

    @Override
    public String toString() {
        return "Symphony{" +
                "title='" + title + '\'' +
                ", description='" + getDescription().orElse("N/A") + '\'' +
                ", createdAt=" + createdAt +
                ", choirs=" + choirs.size() + " items" +
                '}';
    }
}