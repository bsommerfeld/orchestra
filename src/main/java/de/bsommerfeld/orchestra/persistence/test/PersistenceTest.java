package de.bsommerfeld.orchestra.persistence.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.bsommerfeld.orchestra.guice.OrchestraModule;
import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.service.SymphonyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A standalone test class for the Orchestra persistence module.
 * This class demonstrates the functionality of the persistence module by:
 * - Creating, retrieving, updating, and deleting Symphonies
 * - Adding and removing Choirs from Symphonies
 * - Adding and removing Voices from Choirs
 */
public class PersistenceTest {

    private static final String SEPARATOR = "----------------------------------------";
    private static SymphonyService symphonyService;

    public static void main(String[] args) {
        System.out.println("Starting Orchestra Persistence Module Test");
        System.out.println(SEPARATOR);

        // Initialize Guice and get the SymphonyService
        try {
            initializeDependencies();
            System.out.println("✅ Dependencies initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize dependencies: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Clean up any existing test data
        cleanupTestData();

        // Run the test scenarios
        try {
            // Test creating a Symphony
            Symphony symphony = testCreateSymphony();
            
            // Test adding Choirs to the Symphony
            symphony = testAddChoirs(symphony);
            
            // Test adding Voices to the Choirs
            symphony = testAddVoices(symphony);
            
            // Test retrieving the Symphony
            testRetrieveSymphony(symphony.getTitle());
            
            // Test retrieving all Symphonies
            testRetrieveAllSymphonies();
            
            // Test updating the Symphony
            testUpdateSymphony(symphony);
            
            // Test removing a Voice
            testRemoveVoice(symphony.getTitle());
            
            // Test removing a Choir
            testRemoveChoir(symphony.getTitle());
            
            // Test deleting the Symphony
            testDeleteSymphony(symphony.getTitle());
            
            System.out.println(SEPARATOR);
            System.out.println("✅ All tests completed successfully!");
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the Guice dependency injection and gets the SymphonyService.
     */
    private static void initializeDependencies() {
        Injector injector = Guice.createInjector(new OrchestraModule());
        symphonyService = injector.getInstance(SymphonyService.class);
    }

    /**
     * Cleans up any existing test data to ensure a clean test environment.
     */
    private static void cleanupTestData() {
        System.out.println("Cleaning up test data...");
        
        try {
            // Delete the test Symphony if it exists
            symphonyService.deleteSymphony("Test Symphony");
            System.out.println("✅ Test data cleaned up successfully");
        } catch (Exception e) {
            System.out.println("ℹ️ No test data to clean up or error occurred: " + e.getMessage());
        }
        
        System.out.println(SEPARATOR);
    }

    /**
     * Tests creating a new Symphony.
     * 
     * @return The created Symphony
     */
    private static Symphony testCreateSymphony() {
        System.out.println("Testing createSymphony...");
        
        Symphony symphony = symphonyService.createSymphony(
                "Test Symphony", 
                "A test symphony for demonstrating the persistence module"
        );
        
        System.out.println("✅ Symphony created: " + symphony.getTitle());
        System.out.println(SEPARATOR);
        
        return symphony;
    }

    /**
     * Tests adding Choirs to a Symphony.
     * 
     * @param symphony The Symphony to add Choirs to
     * @return The updated Symphony
     */
    private static Symphony testAddChoirs(Symphony symphony) {
        System.out.println("Testing addChoir...");
        
        // Create and add the first Choir
        Choir choir1 = new Choir(
                "Test Choir 1", 
                "The first test choir", 
                Collections.emptyList()
        );
        Symphony updatedSymphony = symphonyService.addChoir(symphony.getTitle(), choir1);
        System.out.println("✅ Added Choir: " + choir1.getName());
        
        // Create and add the second Choir
        Choir choir2 = new Choir(
                "Test Choir 2", 
                "The second test choir", 
                Collections.emptyList()
        );
        updatedSymphony = symphonyService.addChoir(updatedSymphony.getTitle(), choir2);
        System.out.println("✅ Added Choir: " + choir2.getName());
        
        System.out.println("✅ Symphony now has " + updatedSymphony.getChoirs().size() + " choirs");
        System.out.println(SEPARATOR);
        
        return updatedSymphony;
    }

    /**
     * Tests adding Voices to Choirs in a Symphony.
     * 
     * @param symphony The Symphony containing the Choirs to add Voices to
     * @return The updated Symphony
     */
    private static Symphony testAddVoices(Symphony symphony) {
        System.out.println("Testing addVoice...");
        
        // Get the names of the Choirs
        List<String> choirNames = new ArrayList<>();
        for (Choir choir : symphony.getChoirs()) {
            choirNames.add(choir.getName());
        }
        
        // Add Voices to the first Choir
        String firstChoirName = choirNames.get(0);
        
        Voice voice1 = new Voice(
                "Test Voice 1", 
                "The first test voice", 
                Collections.emptyList()
        );
        Symphony updatedSymphony = symphonyService.addVoice(
                symphony.getTitle(), 
                firstChoirName, 
                voice1
        );
        System.out.println("✅ Added Voice: " + voice1.getTitle() + " to Choir: " + firstChoirName);
        
        Voice voice2 = new Voice(
                "Test Voice 2", 
                "The second test voice", 
                Collections.emptyList()
        );
        updatedSymphony = symphonyService.addVoice(
                updatedSymphony.getTitle(), 
                firstChoirName, 
                voice2
        );
        System.out.println("✅ Added Voice: " + voice2.getTitle() + " to Choir: " + firstChoirName);
        
        // Add a Voice to the second Choir
        String secondChoirName = choirNames.get(1);
        
        Voice voice3 = new Voice(
                "Test Voice 3", 
                "The third test voice", 
                Collections.emptyList()
        );
        updatedSymphony = symphonyService.addVoice(
                updatedSymphony.getTitle(), 
                secondChoirName, 
                voice3
        );
        System.out.println("✅ Added Voice: " + voice3.getTitle() + " to Choir: " + secondChoirName);
        
        // Add a nested Voice (sub-voice)
        List<Voice> subVoices = new ArrayList<>();
        subVoices.add(new Voice(
                "Sub-Voice 1", 
                "A nested voice", 
                Collections.emptyList()
        ));
        
        Voice voice4 = new Voice(
                "Test Voice 4", 
                "A voice with sub-voices", 
                subVoices
        );
        updatedSymphony = symphonyService.addVoice(
                updatedSymphony.getTitle(), 
                secondChoirName, 
                voice4
        );
        System.out.println("✅ Added Voice: " + voice4.getTitle() + " with sub-voices to Choir: " + secondChoirName);
        
        System.out.println("✅ Voices added successfully");
        System.out.println(SEPARATOR);
        
        return updatedSymphony;
    }

    /**
     * Tests retrieving a Symphony by its title.
     * 
     * @param title The title of the Symphony to retrieve
     */
    private static void testRetrieveSymphony(String title) {
        System.out.println("Testing getSymphony...");
        
        Optional<Symphony> optionalSymphony = symphonyService.getSymphony(title);
        
        if (optionalSymphony.isPresent()) {
            Symphony symphony = optionalSymphony.get();
            System.out.println("✅ Retrieved Symphony: " + symphony.getTitle());
            System.out.println("  Description: " + symphony.getDescription().orElse("N/A"));
            System.out.println("  Created At: " + symphony.getCreatedAt());
            System.out.println("  Number of Choirs: " + symphony.getChoirs().size());
            
            // Print details of each Choir
            for (Choir choir : symphony.getChoirs()) {
                System.out.println("  Choir: " + choir.getName());
                System.out.println("    Description: " + choir.getDescription().orElse("N/A"));
                System.out.println("    Number of Voices: " + choir.getVoices().size());
                
                // Print details of each Voice
                for (Voice voice : choir.getVoices()) {
                    System.out.println("    Voice: " + voice.getTitle());
                    System.out.println("      Description: " + voice.getDescription().orElse("N/A"));
                    System.out.println("      Number of Sub-Voices: " + voice.getSubVoices().size());
                }
            }
        } else {
            System.err.println("❌ Symphony not found: " + title);
            throw new RuntimeException("Symphony not found: " + title);
        }
        
        System.out.println(SEPARATOR);
    }

    /**
     * Tests retrieving all Symphonies.
     */
    private static void testRetrieveAllSymphonies() {
        System.out.println("Testing getAllSymphonies...");
        
        List<Symphony> symphonies = symphonyService.getAllSymphonies();
        
        System.out.println("✅ Retrieved " + symphonies.size() + " symphonies");
        
        for (Symphony symphony : symphonies) {
            System.out.println("  Symphony: " + symphony.getTitle());
            System.out.println("    Description: " + symphony.getDescription().orElse("N/A"));
            System.out.println("    Number of Choirs: " + symphony.getChoirs().size());
        }
        
        System.out.println(SEPARATOR);
    }

    /**
     * Tests updating a Symphony.
     * 
     * @param symphony The Symphony to update
     */
    private static void testUpdateSymphony(Symphony symphony) {
        System.out.println("Testing updateSymphony...");
        
        // Create a new Symphony with the same title but a different description
        Symphony updatedSymphony = new Symphony(
                symphony.getTitle(),
                "Updated description for the test symphony",
                symphony.getChoirs()
        );
        
        // Update the Symphony
        Symphony result = symphonyService.updateSymphony(updatedSymphony);
        
        System.out.println("✅ Symphony updated: " + result.getTitle());
        System.out.println("  New Description: " + result.getDescription().orElse("N/A"));
        
        System.out.println(SEPARATOR);
    }

    /**
     * Tests removing a Voice from a Choir in a Symphony.
     * 
     * @param symphonyTitle The title of the Symphony
     */
    private static void testRemoveVoice(String symphonyTitle) {
        System.out.println("Testing removeVoice...");
        
        // Get the Symphony
        Optional<Symphony> optionalSymphony = symphonyService.getSymphony(symphonyTitle);
        
        if (optionalSymphony.isPresent()) {
            Symphony symphony = optionalSymphony.get();
            
            // Get the first Choir
            if (!symphony.getChoirs().isEmpty()) {
                Choir choir = symphony.getChoirs().get(0);
                
                // Get the first Voice
                if (!choir.getVoices().isEmpty()) {
                    Voice voice = choir.getVoices().get(0);
                    
                    // Remove the Voice
                    Symphony updatedSymphony = symphonyService.removeVoice(
                            symphonyTitle,
                            choir.getName(),
                            voice.getTitle()
                    );
                    
                    System.out.println("✅ Removed Voice: " + voice.getTitle() + " from Choir: " + choir.getName());
                    
                    // Verify the Voice was removed
                    boolean voiceRemoved = true;
                    for (Choir c : updatedSymphony.getChoirs()) {
                        if (c.getName().equals(choir.getName())) {
                            for (Voice v : c.getVoices()) {
                                if (v.getTitle().equals(voice.getTitle())) {
                                    voiceRemoved = false;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (voiceRemoved) {
                        System.out.println("✅ Voice removal verified");
                    } else {
                        System.err.println("❌ Voice was not removed");
                        throw new RuntimeException("Voice was not removed");
                    }
                } else {
                    System.err.println("❌ No voices found in choir: " + choir.getName());
                    throw new RuntimeException("No voices found in choir: " + choir.getName());
                }
            } else {
                System.err.println("❌ No choirs found in symphony: " + symphonyTitle);
                throw new RuntimeException("No choirs found in symphony: " + symphonyTitle);
            }
        } else {
            System.err.println("❌ Symphony not found: " + symphonyTitle);
            throw new RuntimeException("Symphony not found: " + symphonyTitle);
        }
        
        System.out.println(SEPARATOR);
    }

    /**
     * Tests removing a Choir from a Symphony.
     * 
     * @param symphonyTitle The title of the Symphony
     */
    private static void testRemoveChoir(String symphonyTitle) {
        System.out.println("Testing removeChoir...");
        
        // Get the Symphony
        Optional<Symphony> optionalSymphony = symphonyService.getSymphony(symphonyTitle);
        
        if (optionalSymphony.isPresent()) {
            Symphony symphony = optionalSymphony.get();
            
            // Get the first Choir
            if (!symphony.getChoirs().isEmpty()) {
                Choir choir = symphony.getChoirs().get(0);
                
                // Remove the Choir
                Symphony updatedSymphony = symphonyService.removeChoir(
                        symphonyTitle,
                        choir.getName()
                );
                
                System.out.println("✅ Removed Choir: " + choir.getName());
                
                // Verify the Choir was removed
                boolean choirRemoved = true;
                for (Choir c : updatedSymphony.getChoirs()) {
                    if (c.getName().equals(choir.getName())) {
                        choirRemoved = false;
                        break;
                    }
                }
                
                if (choirRemoved) {
                    System.out.println("✅ Choir removal verified");
                } else {
                    System.err.println("❌ Choir was not removed");
                    throw new RuntimeException("Choir was not removed");
                }
            } else {
                System.err.println("❌ No choirs found in symphony: " + symphonyTitle);
                throw new RuntimeException("No choirs found in symphony: " + symphonyTitle);
            }
        } else {
            System.err.println("❌ Symphony not found: " + symphonyTitle);
            throw new RuntimeException("Symphony not found: " + symphonyTitle);
        }
        
        System.out.println(SEPARATOR);
    }

    /**
     * Tests deleting a Symphony.
     * 
     * @param symphonyTitle The title of the Symphony to delete
     */
    private static void testDeleteSymphony(String symphonyTitle) {
        System.out.println("Testing deleteSymphony...");
        
        // Delete the Symphony
        boolean deleted = symphonyService.deleteSymphony(symphonyTitle);
        
        if (deleted) {
            System.out.println("✅ Symphony deleted: " + symphonyTitle);
            
            // Verify the Symphony was deleted
            Optional<Symphony> optionalSymphony = symphonyService.getSymphony(symphonyTitle);
            
            if (!optionalSymphony.isPresent()) {
                System.out.println("✅ Symphony deletion verified");
            } else {
                System.err.println("❌ Symphony was not deleted");
                throw new RuntimeException("Symphony was not deleted");
            }
        } else {
            System.err.println("❌ Failed to delete Symphony: " + symphonyTitle);
            throw new RuntimeException("Failed to delete Symphony: " + symphonyTitle);
        }
        
        System.out.println(SEPARATOR);
    }
}