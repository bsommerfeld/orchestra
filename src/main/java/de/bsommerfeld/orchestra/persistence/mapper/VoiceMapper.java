package de.bsommerfeld.orchestra.persistence.mapper;

import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.dto.VoiceDTO;

/**
 * Mapper interface for converting between Voice domain models and VoiceDTO objects.
 */
public interface VoiceMapper extends Mapper<Voice, VoiceDTO> {
}