package de.bsommerfeld.orchestra.persistence.mapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.persistence.dto.ChoirDTO;

/**
 * Implementation of the ChoirMapper interface.
 * Handles conversion between Choir domain models and ChoirDTO objects.
 */
@Singleton
public class ChoirMapperImpl implements ChoirMapper {

    private final VoiceMapper voiceMapper;

    /**
     * Constructs a new ChoirMapperImpl with the specified VoiceMapper.
     *
     * @param voiceMapper The VoiceMapper to use for converting Voice objects
     */
    @Inject
    public ChoirMapperImpl(VoiceMapper voiceMapper) {
        this.voiceMapper = voiceMapper;
    }

    @Override
    public ChoirDTO toDto(Choir domain) {
        if (domain == null) {
            return null;
        }

        // Create and return the DTO
        return new ChoirDTO(
                domain.getName(),
                domain.getDescription().orElse(null),
                voiceMapper.toDtoList(domain.getVoices())
        );
    }

    @Override
    public Choir toDomain(ChoirDTO dto) {
        if (dto == null) {
            return null;
        }

        // Create and return the domain model
        return new Choir(
                dto.getName(),
                dto.getDescription(),
                voiceMapper.toDomainList(dto.getVoices())
        );
    }
}