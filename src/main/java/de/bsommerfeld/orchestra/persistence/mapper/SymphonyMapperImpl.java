package de.bsommerfeld.orchestra.persistence.mapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.persistence.dto.SymphonyDTO;

/**
 * Implementation of the SymphonyMapper interface.
 * Handles conversion between Symphony domain models and SymphonyDTO objects.
 */
@Singleton
public class SymphonyMapperImpl implements SymphonyMapper {

    private final ChoirMapper choirMapper;

    /**
     * Constructs a new SymphonyMapperImpl with the specified ChoirMapper.
     *
     * @param choirMapper The ChoirMapper to use for converting Choir objects
     */
    @Inject
    public SymphonyMapperImpl(ChoirMapper choirMapper) {
        this.choirMapper = choirMapper;
    }

    @Override
    public SymphonyDTO toDto(Symphony domain) {
        if (domain == null) {
            return null;
        }

        // Create and return the DTO
        return new SymphonyDTO(
                domain.getTitle(),
                domain.getDescription().orElse(null),
                domain.getCreatedAt(),
                choirMapper.toDtoList(domain.getChoirs())
        );
    }

    @Override
    public Symphony toDomain(SymphonyDTO dto) {
        if (dto == null) {
            return null;
        }

        // Create and return the domain model
        return new Symphony(
                dto.getTitle(),
                dto.getDescription(),
                choirMapper.toDomainList(dto.getChoirs())
        );
    }
}