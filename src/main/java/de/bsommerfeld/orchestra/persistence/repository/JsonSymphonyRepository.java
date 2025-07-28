package de.bsommerfeld.orchestra.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.persistence.dto.SymphonyDTO;
import de.bsommerfeld.orchestra.persistence.mapper.SymphonyMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JSON-based implementation of the SymphonyRepository interface.
 * Stores Symphony objects as JSON files in a designated directory.
 */
@Singleton
public class JsonSymphonyRepository implements SymphonyRepository {

    private static final String STORAGE_DIR = "data/symphonies";
    private static final String FILE_EXTENSION = ".json";

    private final SymphonyMapper symphonyMapper;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new JsonSymphonyRepository with the specified SymphonyMapper.
     *
     * @param symphonyMapper The SymphonyMapper to use for converting Symphony objects
     */
    @Inject
    public JsonSymphonyRepository(SymphonyMapper symphonyMapper) {
        this.symphonyMapper = symphonyMapper;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For handling LocalDateTime

        // Create the storage directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory: " + STORAGE_DIR, e);
        }
    }

    @Override
    public Symphony save(Symphony entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Symphony cannot be null");
        }

        try {
            // Convert the Symphony to a SymphonyDTO
            SymphonyDTO dto = symphonyMapper.toDto(entity);

            // Write the DTO to a JSON file
            File file = getFile(entity.getTitle());
            objectMapper.writeValue(file, dto);

            return entity;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Symphony: " + entity.getTitle(), e);
        }
    }

    @Override
    public Optional<Symphony> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        File file = getFile(id);
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            // Read the JSON file and convert it to a SymphonyDTO
            SymphonyDTO dto = objectMapper.readValue(file, SymphonyDTO.class);

            // Convert the DTO to a Symphony
            Symphony symphony = symphonyMapper.toDomain(dto);

            return Optional.of(symphony);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Symphony: " + id, e);
        }
    }

    @Override
    public List<Symphony> findAll() {
        try {
            // Get all JSON files in the storage directory
            List<Symphony> symphonies = new ArrayList<>();
            Path dirPath = Paths.get(STORAGE_DIR);
            
            if (!Files.exists(dirPath)) {
                return symphonies;
            }

            try (Stream<Path> paths = Files.walk(dirPath)) {
                List<File> files = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(FILE_EXTENSION))
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                // Read each file and convert it to a Symphony
                for (File file : files) {
                    SymphonyDTO dto = objectMapper.readValue(file, SymphonyDTO.class);
                    Symphony symphony = symphonyMapper.toDomain(dto);
                    symphonies.add(symphony);
                }
            }

            return symphonies;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Symphonies", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        File file = getFile(id);
        return file.exists() && file.delete();
    }

    @Override
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        File file = getFile(id);
        return file.exists();
    }

    /**
     * Gets the File object for the specified Symphony title.
     *
     * @param title The title of the Symphony
     * @return The File object
     */
    private File getFile(String title) {
        // Sanitize the title to create a valid filename
        String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
        return Paths.get(STORAGE_DIR, sanitizedTitle + FILE_EXTENSION).toFile();
    }
}