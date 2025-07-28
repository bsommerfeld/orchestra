package de.bsommerfeld.orchestra.persistence.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for CRUD operations on domain models.
 *
 * @param <T> The domain model type
 * @param <ID> The identifier type
 */
public interface Repository<T, ID> {

    /**
     * Saves a domain model.
     *
     * @param entity The domain model to save
     * @return The saved domain model
     */
    T save(T entity);

    /**
     * Finds a domain model by its identifier.
     *
     * @param id The identifier of the domain model to find
     * @return An Optional containing the found domain model, or an empty Optional if not found
     */
    Optional<T> findById(ID id);

    /**
     * Finds all domain models.
     *
     * @return A list of all domain models
     */
    List<T> findAll();

    /**
     * Deletes a domain model by its identifier.
     *
     * @param id The identifier of the domain model to delete
     * @return true if the domain model was deleted, false otherwise
     */
    boolean deleteById(ID id);

    /**
     * Checks if a domain model with the specified identifier exists.
     *
     * @param id The identifier to check
     * @return true if a domain model with the specified identifier exists, false otherwise
     */
    boolean existsById(ID id);
}