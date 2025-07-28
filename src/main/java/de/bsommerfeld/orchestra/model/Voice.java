package de.bsommerfeld.orchestra.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a single "voice" or "part" within the Orchestra task management system.
 * A Voice can be a top-level task or a sub-task (represented by its inclusion in a parent's subVoices list).
 * It encapsulates the core information of a work item and can inherently contain nested sub-voices,
 * reflecting the hierarchical structure of a musical composition where individual parts contribute
 * to the grander whole.
 */
public final class Voice { // Made final as it's an immutable value object

    private final String title;
    private final String description; // Changed to Optional<String> for clarity on absence
    private final List<Voice> subVoices; // Changed to List<Voice> to represent nested structure

    /**
     * Constructs a new Voice instance.
     *
     * @param title The mandatory title of the voice/task. Cannot be null or empty.
     * @param description An optional description of the voice/task. Can be null or empty.
     * @param subVoices A list of sub-voices (sub-tasks) belonging to this voice. Can be null or empty.
     * The list will be defensively copied to ensure immutability.
     * @throws IllegalArgumentException if the title is null or empty.
     */
    public Voice(String title, String description, List<Voice> subVoices) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Voice title cannot be null or empty.");
        }
        this.title = title;
        this.description = description; // Will be handled by Optional in getter
        this.subVoices = (subVoices != null) ? List.copyOf(subVoices) : Collections.emptyList(); // Defensive copy and immutability
    }

    /**
     * Returns the title of this voice/task.
     *
     * @return The title of the voice.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the optional description of this voice/task.
     *
     * @return An {@link Optional} containing the description, or an empty Optional if no description is present.
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * Returns an immutable list of sub-voices (sub-tasks) belonging to this voice.
     *
     * @return An unmodifiable {@link List} of {@link Voice} objects. Returns an empty list if no sub-voices exist.
     */
    public List<Voice> getSubVoices() {
        return subVoices; // Already an immutable copy from constructor
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voice voice = (Voice) o;
        return Objects.equals(title, voice.title) &&
                Objects.equals(description, voice.description) && // Compare the raw description field
                Objects.equals(subVoices, voice.subVoices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, subVoices);
    }

    @Override
    public String toString() {
        return "Voice{" +
                "title='" + title + '\'' +
                ", description='" + getDescription().orElse("N/A") + '\'' + // Use Optional for toString
                ", subVoices=" + subVoices.size() + " items" +
                '}';
    }
}