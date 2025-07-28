package de.bsommerfeld.orchestra.persistence.mapper;

import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.dto.VoiceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the VoiceMapper interface.
 * Handles conversion between Voice domain models and VoiceDTO objects.
 */
@Singleton
public class VoiceMapperImpl implements VoiceMapper {

    @Override
    public VoiceDTO toDto(Voice domain) {
        if (domain == null) {
            return null;
        }

        // Convert subVoices recursively
        List<VoiceDTO> subVoiceDTOs = null;
        if (!domain.getSubVoices().isEmpty()) {
            subVoiceDTOs = domain.getSubVoices().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        // Create and return the DTO
        return new VoiceDTO(
                domain.getTitle(),
                domain.getDescription().orElse(null),
                subVoiceDTOs
        );
    }

    @Override
    public Voice toDomain(VoiceDTO dto) {
        if (dto == null) {
            return null;
        }

        // Convert subVoices recursively
        List<Voice> subVoices = null;
        if (dto.getSubVoices() != null && !dto.getSubVoices().isEmpty()) {
            subVoices = dto.getSubVoices().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        }

        // Create and return the domain model
        return new Voice(
                dto.getTitle(),
                dto.getDescription(),
                subVoices
        );
    }
}