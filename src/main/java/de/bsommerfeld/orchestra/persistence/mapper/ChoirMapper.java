package de.bsommerfeld.orchestra.persistence.mapper;

import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.persistence.dto.ChoirDTO;

/**
 * Mapper interface for converting between Choir domain models and ChoirDTO objects.
 */
public interface ChoirMapper extends Mapper<Choir, ChoirDTO> {
}