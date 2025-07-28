package de.bsommerfeld.orchestra.persistence.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.repository.SymphonyRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the SymphonyService interface.
 * Provides business operations for working with Symphony objects.
 */
@Singleton
public class SymphonyServiceImpl implements SymphonyService {

    private final SymphonyRepository symphonyRepository;

    /**
     * Constructs a new SymphonyServiceImpl with the specified SymphonyRepository.
     *
     * @param symphonyRepository The SymphonyRepository to use for data access
     */
    @Inject
    public SymphonyServiceImpl(SymphonyRepository symphonyRepository) {
        this.symphonyRepository = symphonyRepository;
    }

    @Override
    public Symphony createSymphony(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Symphony title cannot be null or empty");
        }

        if (symphonyRepository.existsById(title)) {
            throw new IllegalArgumentException("Symphony with title '" + title + "' already exists");
        }

        Symphony symphony = new Symphony(title, description, Collections.emptyList());
        return symphonyRepository.save(symphony);
    }

    @Override
    public Optional<Symphony> getSymphony(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Optional.empty();
        }

        return symphonyRepository.findById(title);
    }

    @Override
    public List<Symphony> getAllSymphonies() {
        return symphonyRepository.findAll();
    }

    @Override
    public Symphony updateSymphony(Symphony symphony) {
        if (symphony == null) {
            throw new IllegalArgumentException("Symphony cannot be null");
        }

        if (!symphonyRepository.existsById(symphony.getTitle())) {
            throw new IllegalArgumentException("Symphony with title '" + symphony.getTitle() + "' does not exist");
        }

        return symphonyRepository.save(symphony);
    }

    @Override
    public boolean deleteSymphony(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }

        return symphonyRepository.deleteById(title);
    }

    @Override
    public Symphony addChoir(String symphonyTitle, Choir choir) {
        if (symphonyTitle == null || symphonyTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Symphony title cannot be null or empty");
        }

        if (choir == null) {
            throw new IllegalArgumentException("Choir cannot be null");
        }

        Optional<Symphony> optionalSymphony = symphonyRepository.findById(symphonyTitle);
        if (!optionalSymphony.isPresent()) {
            throw new IllegalArgumentException("Symphony with title '" + symphonyTitle + "' does not exist");
        }

        Symphony symphony = optionalSymphony.get();
        
        // Check if a choir with the same name already exists
        boolean choirExists = symphony.getChoirs().stream()
                .anyMatch(c -> c.getName().equals(choir.getName()));
        if (choirExists) {
            throw new IllegalArgumentException("Choir with name '" + choir.getName() + "' already exists in this Symphony");
        }

        // Create a new list with the existing choirs plus the new one
        List<Choir> updatedChoirs = new ArrayList<>(symphony.getChoirs());
        updatedChoirs.add(choir);

        // Create a new Symphony with the updated choirs
        Symphony updatedSymphony = new Symphony(
                symphony.getTitle(),
                symphony.getDescription().orElse(null),
                updatedChoirs
        );

        return symphonyRepository.save(updatedSymphony);
    }

    @Override
    public Symphony removeChoir(String symphonyTitle, String choirName) {
        if (symphonyTitle == null || symphonyTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Symphony title cannot be null or empty");
        }

        if (choirName == null || choirName.trim().isEmpty()) {
            throw new IllegalArgumentException("Choir name cannot be null or empty");
        }

        Optional<Symphony> optionalSymphony = symphonyRepository.findById(symphonyTitle);
        if (!optionalSymphony.isPresent()) {
            throw new IllegalArgumentException("Symphony with title '" + symphonyTitle + "' does not exist");
        }

        Symphony symphony = optionalSymphony.get();
        
        // Check if the choir exists
        boolean choirExists = symphony.getChoirs().stream()
                .anyMatch(c -> c.getName().equals(choirName));
        if (!choirExists) {
            throw new IllegalArgumentException("Choir with name '" + choirName + "' does not exist in this Symphony");
        }

        // Create a new list without the choir to remove
        List<Choir> updatedChoirs = symphony.getChoirs().stream()
                .filter(c -> !c.getName().equals(choirName))
                .collect(Collectors.toList());

        // Create a new Symphony with the updated choirs
        Symphony updatedSymphony = new Symphony(
                symphony.getTitle(),
                symphony.getDescription().orElse(null),
                updatedChoirs
        );

        return symphonyRepository.save(updatedSymphony);
    }

    @Override
    public Symphony addVoice(String symphonyTitle, String choirName, Voice voice) {
        if (symphonyTitle == null || symphonyTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Symphony title cannot be null or empty");
        }

        if (choirName == null || choirName.trim().isEmpty()) {
            throw new IllegalArgumentException("Choir name cannot be null or empty");
        }

        if (voice == null) {
            throw new IllegalArgumentException("Voice cannot be null");
        }

        Optional<Symphony> optionalSymphony = symphonyRepository.findById(symphonyTitle);
        if (!optionalSymphony.isPresent()) {
            throw new IllegalArgumentException("Symphony with title '" + symphonyTitle + "' does not exist");
        }

        Symphony symphony = optionalSymphony.get();
        
        // Find the choir
        Optional<Choir> optionalChoir = symphony.getChoirs().stream()
                .filter(c -> c.getName().equals(choirName))
                .findFirst();
        if (!optionalChoir.isPresent()) {
            throw new IllegalArgumentException("Choir with name '" + choirName + "' does not exist in this Symphony");
        }

        Choir choir = optionalChoir.get();
        
        // Check if a voice with the same title already exists
        boolean voiceExists = choir.getVoices().stream()
                .anyMatch(v -> v.getTitle().equals(voice.getTitle()));
        if (voiceExists) {
            throw new IllegalArgumentException("Voice with title '" + voice.getTitle() + "' already exists in this Choir");
        }

        // Create a new list with the existing voices plus the new one
        List<Voice> updatedVoices = new ArrayList<>(choir.getVoices());
        updatedVoices.add(voice);

        // Create a new Choir with the updated voices
        Choir updatedChoir = new Choir(
                choir.getName(),
                choir.getDescription().orElse(null),
                updatedVoices
        );

        // Create a new list of choirs with the updated choir
        List<Choir> updatedChoirs = symphony.getChoirs().stream()
                .map(c -> c.getName().equals(choirName) ? updatedChoir : c)
                .collect(Collectors.toList());

        // Create a new Symphony with the updated choirs
        Symphony updatedSymphony = new Symphony(
                symphony.getTitle(),
                symphony.getDescription().orElse(null),
                updatedChoirs
        );

        return symphonyRepository.save(updatedSymphony);
    }

    @Override
    public Symphony removeVoice(String symphonyTitle, String choirName, String voiceTitle) {
        if (symphonyTitle == null || symphonyTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Symphony title cannot be null or empty");
        }

        if (choirName == null || choirName.trim().isEmpty()) {
            throw new IllegalArgumentException("Choir name cannot be null or empty");
        }

        if (voiceTitle == null || voiceTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Voice title cannot be null or empty");
        }

        Optional<Symphony> optionalSymphony = symphonyRepository.findById(symphonyTitle);
        if (!optionalSymphony.isPresent()) {
            throw new IllegalArgumentException("Symphony with title '" + symphonyTitle + "' does not exist");
        }

        Symphony symphony = optionalSymphony.get();
        
        // Find the choir
        Optional<Choir> optionalChoir = symphony.getChoirs().stream()
                .filter(c -> c.getName().equals(choirName))
                .findFirst();
        if (!optionalChoir.isPresent()) {
            throw new IllegalArgumentException("Choir with name '" + choirName + "' does not exist in this Symphony");
        }

        Choir choir = optionalChoir.get();
        
        // Check if the voice exists
        boolean voiceExists = choir.getVoices().stream()
                .anyMatch(v -> v.getTitle().equals(voiceTitle));
        if (!voiceExists) {
            throw new IllegalArgumentException("Voice with title '" + voiceTitle + "' does not exist in this Choir");
        }

        // Create a new list without the voice to remove
        List<Voice> updatedVoices = choir.getVoices().stream()
                .filter(v -> !v.getTitle().equals(voiceTitle))
                .collect(Collectors.toList());

        // Create a new Choir with the updated voices
        Choir updatedChoir = new Choir(
                choir.getName(),
                choir.getDescription().orElse(null),
                updatedVoices
        );

        // Create a new list of choirs with the updated choir
        List<Choir> updatedChoirs = symphony.getChoirs().stream()
                .map(c -> c.getName().equals(choirName) ? updatedChoir : c)
                .collect(Collectors.toList());

        // Create a new Symphony with the updated choirs
        Symphony updatedSymphony = new Symphony(
                symphony.getTitle(),
                symphony.getDescription().orElse(null),
                updatedChoirs
        );

        return symphonyRepository.save(updatedSymphony);
    }
}