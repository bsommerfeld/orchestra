package de.bsommerfeld.orchestra.ui.controller;

import com.google.inject.Inject;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.persistence.service.SymphonyService;
import de.bsommerfeld.orchestra.ui.view.StageProvider;
import de.bsommerfeld.orchestra.ui.view.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controller for the project selection screen.
 * Displays a list of available projects and allows the user to select one.
 * When a project is selected, a new stage is opened to display the project.
 */
@View
public class ProjectSelectionController {

    private final StageProvider stageProvider;
    private final SymphonyService symphonyService;
    
    @FXML
    private ListView<String> projectListView;
    
    private ObservableList<String> projects = FXCollections.observableArrayList();
    
    @Inject
    public ProjectSelectionController(StageProvider stageProvider, SymphonyService symphonyService) {
        this.stageProvider = stageProvider;
        this.symphonyService = symphonyService;
    }
    
    @FXML
    public void initialize() {
        // Initialize the list view with the projects
        projectListView.setItems(projects);
        
        // Load projects (this is a simple implementation that could be enhanced)
        loadProjects();
    }
    
    /**
     * Loads the list of available projects from the SymphonyService.
     * This method retrieves all Symphony objects from the persistence layer
     * and adds their titles to the projects list.
     */
    private void loadProjects() {
        // Clear the existing projects list
        projects.clear();
        
        // Load all Symphony objects from the SymphonyService
        List<Symphony> symphonies = symphonyService.getAllSymphonies();
        
        if (symphonies.isEmpty()) {
            // If no projects are found, add a message to create a new project
            projects.add("No projects found. Create a new project.");
        } else {
            // Add the titles of all Symphony objects to the projects list
            for (Symphony symphony : symphonies) {
                projects.add(symphony.getTitle());
            }
        }
    }
    
    /**
     * Handles the selection of a project from the list.
     * When a project is double-clicked, a new stage is opened to display the project.
     *
     * @param event the mouse event that triggered the selection
     */
    @FXML
    public void onProjectSelected(MouseEvent event) {
        if (event.getClickCount() == 2) {  // Check for double-click
            String selectedProject = projectListView.getSelectionModel().getSelectedItem();
            if (selectedProject != null) {
                openProject(selectedProject);
            }
        }
    }
    
    /**
     * Opens a new stage for the selected project.
     * Verifies that the project exists in the SymphonyService before opening it.
     *
     * @param projectName the name of the project to open
     */
    private void openProject(String projectName) {
        // Skip if the message about no projects is selected
        if (projectName.equals("No projects found. Create a new project.")) {
            // TODO: Implement project creation functionality
            return;
        }
        
        // Verify that the project exists in the SymphonyService
        if (symphonyService.getSymphony(projectName).isPresent()) {
            // Create a unique stage name for this project
            String stageName = "project-" + projectName.replaceAll("\\s+", "-").toLowerCase();
            
            // Create a new stage for the project
            stageProvider.createStage(stageName, stage -> {
                stage.setTitle(projectName);
                stage.setWidth(800);
                stage.setHeight(600);
            });
            
            // Show the MetaController in the new stage with the project name
            // The MetaController will load the Symphony object using the SymphonyService
            stageProvider.showView(stageName, MetaController.class, projectName);
        }
    }
}