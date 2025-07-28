package de.bsommerfeld.orchestra.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic mapper interface for converting between domain models and DTOs.
 *
 * @param <D> The domain model type
 * @param <T> The DTO type
 */
public interface Mapper<D, T> {

    /**
     * Converts a domain model to a DTO.
     *
     * @param domain The domain model to convert
     * @return The resulting DTO
     */
    T toDto(D domain);

    /**
     * Converts a DTO to a domain model.
     *
     * @param dto The DTO to convert
     * @return The resulting domain model
     */
    D toDomain(T dto);

    /**
     * Converts a list of domain models to a list of DTOs.
     *
     * @param domainList The list of domain models to convert
     * @return The resulting list of DTOs
     */
    default List<T> toDtoList(List<D> domainList) {
        if (domainList == null) {
            return null;
        }
        return domainList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of DTOs to a list of domain models.
     *
     * @param dtoList The list of DTOs to convert
     * @return The resulting list of domain models
     */
    default List<D> toDomainList(List<T> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}