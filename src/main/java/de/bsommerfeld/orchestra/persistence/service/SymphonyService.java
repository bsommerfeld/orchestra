package de.bsommerfeld.orchestra.persistence.service;

import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for operations on Symphony objects.
 * Provides a higher-level API than the repository, focusing on business operations.
 */
public interface SymphonyService {

    /**
     * Creates a new Symphony.
     *
     * @param title The title of the Symphony
     * @param description The description of the Symphony (can be null)
     * @return The created Symphony
     * @throws IllegalArgumentException if a Symphony with the same title already exists
     */
    Symphony createSymphony(String title, String description);

    /**
     * Retrieves a Symphony by its title.
     *
     * @param title The title of the Symphony to retrieve
     * @return An Optional containing the Symphony, or an empty Optional if not found
     */
    Optional<Symphony> getSymphony(String title);

    /**
     * Retrieves all Symphonies.
     *
     * @return A list of all Symphonies
     */
    List<Symphony> getAllSymphonies();

    /**
     * Updates an existing Symphony.
     *
     * @param symphony The Symphony to update
     * @return The updated Symphony
     * @throws IllegalArgumentException if the Symphony does not exist
     */
    Symphony updateSymphony(Symphony symphony);

    /**
     * Deletes a Symphony by its title.
     *
     * @param title The title of the Symphony to delete
     * @return true if the Symphony was deleted, false otherwise
     */
    boolean deleteSymphony(String title);

    /**
     * Adds a Choir to a Symphony.
     *
     * @param symphonyTitle The title of the Symphony to add the Choir to
     * @param choir The Choir to add
     * @return The updated Symphony
     * @throws IllegalArgumentException if the Symphony does not exist
     */
    Symphony addChoir(String symphonyTitle, Choir choir);

    /**
     * Removes a Choir from a Symphony.
     *
     * @param symphonyTitle The title of the Symphony to remove the Choir from
     * @param choirName The name of the Choir to remove
     * @return The updated Symphony
     * @throws IllegalArgumentException if the Symphony or Choir does not exist
     */
    Symphony removeChoir(String symphonyTitle, String choirName);

    /**
     * Adds a Voice to a Choir in a Symphony.
     *
     * @param symphonyTitle The title of the Symphony
     * @param choirName The name of the Choir to add the Voice to
     * @param voice The Voice to add
     * @return The updated Symphony
     * @throws IllegalArgumentException if the Symphony or Choir does not exist
     */
    Symphony addVoice(String symphonyTitle, String choirName, Voice voice);

    /**
     * Removes a Voice from a Choir in a Symphony.
     *
     * @param symphonyTitle The title of the Symphony
     * @param choirName The name of the Choir to remove the Voice from
     * @param voiceTitle The title of the Voice to remove
     * @return The updated Symphony
     * @throws IllegalArgumentException if the Symphony, Choir, or Voice does not exist
     */
    Symphony removeVoice(String symphonyTitle, String choirName, String voiceTitle);
}