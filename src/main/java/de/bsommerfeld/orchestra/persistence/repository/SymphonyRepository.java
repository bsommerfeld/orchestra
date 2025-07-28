package de.bsommerfeld.orchestra.persistence.repository;

import de.bsommerfeld.orchestra.model.Symphony;

/**
 * Repository interface for CRUD operations on Symphony domain models.
 * Uses the Symphony's title as the identifier.
 */
public interface SymphonyRepository extends Repository<Symphony, String> {
    
    /**
     * Finds a Symphony by its title.
     * This is equivalent to findById since the title is used as the identifier.
     *
     * @param title The title of the Symphony to find
     * @return The found Symphony, or null if not found
     */
    default Symphony findByTitle(String title) {
        return findById(title).orElse(null);
    }
}