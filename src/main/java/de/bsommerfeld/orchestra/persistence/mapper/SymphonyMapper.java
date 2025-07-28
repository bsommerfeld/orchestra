package de.bsommerfeld.orchestra.persistence.mapper;

import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.persistence.dto.SymphonyDTO;

/**
 * Mapper interface for converting between Symphony domain models and SymphonyDTO objects.
 */
public interface SymphonyMapper extends Mapper<Symphony, SymphonyDTO> {
}