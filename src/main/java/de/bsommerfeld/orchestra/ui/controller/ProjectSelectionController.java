package de.bsommerfeld.orchestra.ui.controller;

import com.google.inject.Inject;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.persistence.service.SymphonyService;
import de.bsommerfeld.orchestra.ui.view.StageProvider;
import de.bsommerfeld.orchestra.ui.view.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Optional;

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
    
    @FXML
    private Button newProjectButton;
    
    @FXML
    private Button openProjectButton;
    
    @FXML
    private Button deleteProjectButton;
    
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
     * Handles the action when the "New Project" button is clicked.
     * Opens a dialog to enter a project name and creates a new project.
     */
    @FXML
    public void onNewProject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a new project");
        dialog.setContentText("Please enter the project name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(projectName -> {
            if (!projectName.trim().isEmpty()) {
                try {
                    // Create a new Symphony with the given name and no description
                    symphonyService.createSymphony(projectName, null);
                    
                    // Refresh the project list
                    loadProjects();
                    
                    // Show a confirmation message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Project Created");
                    alert.setHeaderText(null);
                    alert.setContentText("Project '" + projectName + "' has been created successfully.");
                    alert.showAndWait();
                } catch (IllegalArgumentException e) {
                    // Show an error message if the project already exists
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to create project: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }
    
    /**
     * Handles the action when the "Open Project" button is clicked.
     * Opens the selected project in a new stage.
     */
    @FXML
    public void onOpenProject() {
        String selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            openProject(selectedProject);
        } else {
            // Show a message to select a project first
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a project to open.");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles the action when the "Delete Project" button is clicked.
     * Deletes the selected project after confirmation.
     */
    @FXML
    public void onDeleteProject() {
        String selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject != null && !selectedProject.equals("No projects found. Create a new project.")) {
            // Ask for confirmation before deleting
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Project");
            alert.setHeaderText("Delete Project: " + selectedProject);
            alert.setContentText("Are you sure you want to delete this project? This action cannot be undone.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete the project
                boolean deleted = symphonyService.deleteSymphony(selectedProject);
                
                if (deleted) {
                    // Refresh the project list
                    loadProjects();
                    
                    // Show a confirmation message
                    Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
                    confirmAlert.setTitle("Project Deleted");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Project '" + selectedProject + "' has been deleted successfully.");
                    confirmAlert.showAndWait();
                } else {
                    // Show an error message if deletion failed
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete project: " + selectedProject);
                    errorAlert.showAndWait();
                }
            }
        } else if (selectedProject != null && selectedProject.equals("No projects found. Create a new project.")) {
            // Show a message that there's no project to delete
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Projects");
            alert.setHeaderText(null);
            alert.setContentText("There are no projects to delete.");
            alert.showAndWait();
        } else {
            // Show a message to select a project first
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a project to delete.");
            alert.showAndWait();
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
            onNewProject();
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
            
            // Show the ProjectController in the new stage with the project name
            stageProvider.showView(stageName, ProjectController.class, projectName, null, controller -> {
                // Configure the ProjectController to load the project
                ((ProjectController) controller).loadProject(projectName);
            });
        }
    }
}