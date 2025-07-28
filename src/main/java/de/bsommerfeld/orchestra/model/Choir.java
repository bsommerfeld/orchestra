package de.bsommerfeld.orchestra.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a "Choir" in the Orchestra task management system, serving as a list or collection of tasks.
 * A Choir groups related "Voices" (individual tasks/sub-tasks) together within a larger "Symphony" (project).
 * It acts as a cohesive section of work, much like a musical choir contributes a specific part to a symphony.
 * Choirs are immutable once created.
 */
public final class Choir { // Class name changed from Chor to Choir

    private final String name;
    private final String description;
    private final List<Voice> voices;

    /**
     * Constructs a new Choir instance.
     *
     * @param name The mandatory name of this choir/task list. Cannot be null or empty.
     * @param description An optional description of this choir/task list. Can be null or empty.
     * @param voices A list of Voices (tasks) belonging to this choir. Can be null or empty.
     * The list will be defensively copied to ensure immutability.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public Choir(String name, String description, List<Voice> voices) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Choir name cannot be null or empty.");
        }
        this.name = name;
        this.description = description;
        this.voices = (voices != null) ? List.copyOf(voices) : Collections.emptyList();
    }

    /**
     * Returns the name of this choir/task list.
     *
     * @return The name of the choir.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the optional description of this choir/task list.
     *
     * @return An {@link Optional} containing the description, or an empty Optional if no description is present.
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * Returns an immutable list of Voices (tasks) belonging to this choir.
     *
     * @return An unmodifiable {@link List} of {@link Voice} objects. Returns an empty list if no voices exist.
     */
    public List<Voice> getVoices() {
        return voices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Choir choir = (Choir) o; // Cast type changed to Choir
        return Objects.equals(name, choir.name) &&
                Objects.equals(description, choir.description) &&
                Objects.equals(voices, choir.voices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, voices);
    }

    @Override
    public String toString() {
        return "Choir{" +
                "name='" + name + '\'' +
                ", description='" + getDescription().orElse("N/A") + '\'' +
                ", voices=" + voices.size() + " items" +
                '}';
    }
}