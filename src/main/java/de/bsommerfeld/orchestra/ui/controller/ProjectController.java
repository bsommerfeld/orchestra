package de.bsommerfeld.orchestra.ui.controller;

import com.google.inject.Inject;
import de.bsommerfeld.orchestra.model.Choir;
import de.bsommerfeld.orchestra.model.Symphony;
import de.bsommerfeld.orchestra.model.Voice;
import de.bsommerfeld.orchestra.persistence.service.SymphonyService;
import de.bsommerfeld.orchestra.ui.view.StageProvider;
import de.bsommerfeld.orchestra.ui.view.View;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the project view.
 * Displays a Symphony (project) with its Choirs and Voices in a tree structure.
 * Allows for drag and drop of Voices and updates the Symphony live.
 */
@View
public class ProjectController {

    private final StageProvider stageProvider;
    private final SymphonyService symphonyService;
    
    private Symphony symphony;
    private String projectName;
    
    @FXML
    private Label projectTitleLabel;
    
    @FXML
    private Label projectDescriptionLabel;
    
    @FXML
    private VBox choirsContainer;
    
    @FXML
    private VBox choirsList;
    
    @FXML
    private Button addChoirButton;
    
    @FXML
    private TreeView<String> voicesTreeView;
    
    // Map to store the relationship between tree items and model objects
    private final Map<TreeItem<String>, Object> itemToModelMap = new HashMap<>();
    private final Map<String, TreeItem<String>> pathToItemMap = new HashMap<>();
    
    @Inject
    public ProjectController(StageProvider stageProvider, SymphonyService symphonyService) {
        this.stageProvider = stageProvider;
        this.symphonyService = symphonyService;
    }
    
    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Set up the TreeView with CSS for indentation lines
        voicesTreeView.getStyleClass().add("voices-tree-view");
        
        // Enable multiple selection
        voicesTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Set up drag and drop
        setupDragAndDrop();
    }
    
    /**
     * Sets up drag and drop functionality for the TreeView.
     */
    private void setupDragAndDrop() {
        // Set up drag detection
        voicesTreeView.setOnDragDetected(event -> {
            TreeItem<String> selectedItem = voicesTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && itemToModelMap.get(selectedItem) instanceof Voice) {
                Dragboard db = voicesTreeView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem.getValue());
                db.setContent(content);
                event.consume();
            }
        });
        
        // Set up drag over
        voicesTreeView.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                TreeItem<String> targetItem = voicesTreeView.getSelectionModel().getSelectedItem();
                if (targetItem != null) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });
        
        // Set up drag drop
        voicesTreeView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasString()) {
                String sourcePath = db.getString();
                TreeItem<String> sourceItem = pathToItemMap.get(sourcePath);
                TreeItem<String> targetItem = voicesTreeView.getSelectionModel().getSelectedItem();
                
                if (sourceItem != null && targetItem != null) {
                    // Handle the drop
                    handleDrop(sourceItem, targetItem);
                    success = true;
                }
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    /**
     * Handles dropping a source item onto a target item.
     * 
     * @param sourceItem the source item being dragged
     * @param targetItem the target item being dropped on
     */
    private void handleDrop(TreeItem<String> sourceItem, TreeItem<String> targetItem) {
        // Get the model objects
        Object sourceModel = itemToModelMap.get(sourceItem);
        Object targetModel = itemToModelMap.get(targetItem);
        
        // Only allow dropping Voice onto Voice or Choir
        if (sourceModel instanceof Voice) {
            Voice sourceVoice = (Voice) sourceModel;
            
            // Remove from parent
            TreeItem<String> sourceParent = sourceItem.getParent();
            sourceParent.getChildren().remove(sourceItem);
            
            // Add to target
            targetItem.getChildren().add(sourceItem);
            targetItem.setExpanded(true);
            
            // Update the model
            updateModelAfterDrop(sourceVoice, sourceParent, targetItem);
            
            // Save changes
            symphonyService.updateSymphony(symphony);
        }
    }
    
    /**
     * Updates the model after a drag and drop operation.
     * 
     * @param sourceVoice the Voice being moved
     * @param sourceParent the parent TreeItem of the source
     * @param targetItem the target TreeItem
     */
    private void updateModelAfterDrop(Voice sourceVoice, TreeItem<String> sourceParent, TreeItem<String> targetItem) {
        // Get the target model object
        Object targetModel = itemToModelMap.get(targetItem);
        Object sourceParentModel = itemToModelMap.get(sourceParent);
        
        // Handle different target types
        if (targetModel instanceof Choir) {
            // Moving Voice to a Choir
            Choir targetChoir = (Choir) targetModel;
            
            // Remove the Voice from its current parent
            if (sourceParentModel instanceof Choir) {
                // Remove from source Choir
                Choir sourceChoir = (Choir) sourceParentModel;
                symphony = symphonyService.removeVoice(symphony.getTitle(), sourceChoir.getName(), sourceVoice.getTitle());
                
                // Add to target Choir
                symphony = symphonyService.addVoice(symphony.getTitle(), targetChoir.getName(), sourceVoice);
            } else if (sourceParentModel instanceof Voice) {
                // Remove from source Voice (parent Voice)
                Voice parentVoice = (Voice) sourceParentModel;
                
                // Create a new list of subVoices without the source Voice
                List<Voice> updatedSubVoices = parentVoice.getSubVoices().stream()
                        .filter(v -> !v.getTitle().equals(sourceVoice.getTitle()))
                        .collect(Collectors.toList());
                
                // Create a new parent Voice without the source Voice
                Voice updatedParentVoice = new Voice(
                        parentVoice.getTitle(),
                        parentVoice.getDescription().orElse(null),
                        updatedSubVoices,
                        parentVoice.isCompleted()
                );
                
                // Find the Choir containing the parent Voice and update it
                for (Choir choir : symphony.getChoirs()) {
                    updateVoiceInChoir(choir, parentVoice, updatedParentVoice);
                }
                
                // Add the source Voice to the target Choir
                symphony = symphonyService.addVoice(symphony.getTitle(), targetChoir.getName(), sourceVoice);
            }
        } else if (targetModel instanceof Voice) {
            // Moving Voice to another Voice
            Voice targetVoice = (Voice) targetModel;
            
            // Remove the Voice from its current parent
            if (sourceParentModel instanceof Choir) {
                // Remove from source Choir
                Choir sourceChoir = (Choir) sourceParentModel;
                symphony = symphonyService.removeVoice(symphony.getTitle(), sourceChoir.getName(), sourceVoice.getTitle());
                
                // Add to target Voice's subVoices
                // Create a new list with the existing subVoices plus the new one
                List<Voice> updatedSubVoices = new ArrayList<>(targetVoice.getSubVoices());
                updatedSubVoices.add(sourceVoice);
                
                // Create a new Voice with the updated subVoices
                Voice updatedTargetVoice = new Voice(
                        targetVoice.getTitle(),
                        targetVoice.getDescription().orElse(null),
                        updatedSubVoices,
                        targetVoice.isCompleted()
                );
                
                // Find the Choir containing the target Voice and update it
                for (Choir choir : symphony.getChoirs()) {
                    updateVoiceInChoir(choir, targetVoice, updatedTargetVoice);
                }
            } else if (sourceParentModel instanceof Voice) {
                // Remove from source Voice (parent Voice)
                Voice parentVoice = (Voice) sourceParentModel;
                
                // Create a new list of subVoices without the source Voice
                List<Voice> updatedSourceSubVoices = parentVoice.getSubVoices().stream()
                        .filter(v -> !v.getTitle().equals(sourceVoice.getTitle()))
                        .collect(Collectors.toList());
                
                // Create a new parent Voice without the source Voice
                Voice updatedParentVoice = new Voice(
                        parentVoice.getTitle(),
                        parentVoice.getDescription().orElse(null),
                        updatedSourceSubVoices,
                        parentVoice.isCompleted()
                );

                // Create a new list with the target Voice's existing subVoices plus the new one
                List<Voice> updatedTargetSubVoices = new ArrayList<>(targetVoice.getSubVoices());
                updatedTargetSubVoices.add(sourceVoice);
                
                // Create a new target Voice with the updated subVoices
                Voice updatedTargetVoice = new Voice(
                        targetVoice.getTitle(),
                        targetVoice.getDescription().orElse(null),
                        updatedTargetSubVoices,
                        targetVoice.isCompleted()
                );
                
                // Find the Choir containing the parent and target Voices and update them
                for (Choir choir : symphony.getChoirs()) {
                    updateVoiceInChoir(choir, parentVoice, updatedParentVoice);
                    updateVoiceInChoir(choir, targetVoice, updatedTargetVoice);
                }
            }
        }
        
        // Save the updated Symphony
        symphony = symphonyService.updateSymphony(symphony);
    }
    
    /**
     * Helper method to update a Voice in a Choir or in another Voice's subVoices.
     * 
     * @param choir the Choir to search in
     * @param oldVoice the Voice to replace
     * @param newVoice the new Voice
     * @return true if the Voice was found and updated, false otherwise
     */
    private boolean updateVoiceInChoir(Choir choir, Voice oldVoice, Voice newVoice) {
        // Check if the Voice is directly in the Choir
        for (int i = 0; i < choir.getVoices().size(); i++) {
            Voice voice = choir.getVoices().get(i);
            if (voice.getTitle().equals(oldVoice.getTitle())) {
                // Create a new list with the updated Voice
                List<Voice> updatedVoices = new ArrayList<>(choir.getVoices());
                updatedVoices.set(i, newVoice);
                
                // Create a new Choir with the updated Voices
                Choir updatedChoir = new Choir(
                        choir.getName(),
                        choir.getDescription().orElse(null),
                        updatedVoices
                );
                
                // Create a new list of Choirs with the updated Choir
                List<Choir> updatedChoirs = symphony.getChoirs().stream()
                        .map(c -> c.getName().equals(choir.getName()) ? updatedChoir : c)
                        .collect(Collectors.toList());
                
                // Create a new Symphony with the updated Choirs
                symphony = new Symphony(
                        symphony.getTitle(),
                        symphony.getDescription().orElse(null),
                        updatedChoirs
                );
                
                return true;
            }
            
            // Recursively check in the Voice's subVoices
            if (updateVoiceInSubVoices(choir, voice, oldVoice, newVoice)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Helper method to update a Voice in another Voice's subVoices.
     * 
     * @param choir the Choir containing the parent Voice
     * @param parentVoice the parent Voice to search in
     * @param oldVoice the Voice to replace
     * @param newVoice the new Voice
     * @return true if the Voice was found and updated, false otherwise
     */
    private boolean updateVoiceInSubVoices(Choir choir, Voice parentVoice, Voice oldVoice, Voice newVoice) {
        // Check if the Voice is in the parent Voice's subVoices
        for (int i = 0; i < parentVoice.getSubVoices().size(); i++) {
            Voice subVoice = parentVoice.getSubVoices().get(i);
            if (subVoice.getTitle().equals(oldVoice.getTitle())) {
                // Create a new list with the updated Voice
                List<Voice> updatedSubVoices = new ArrayList<>(parentVoice.getSubVoices());
                updatedSubVoices.set(i, newVoice);
                
                // Create a new parent Voice with the updated subVoices
                Voice updatedParentVoice = new Voice(
                        parentVoice.getTitle(),
                        parentVoice.getDescription().orElse(null),
                        updatedSubVoices,
                        parentVoice.isCompleted()
                );
                
                // Update the parent Voice in the Choir
                updateVoiceInChoir(choir, parentVoice, updatedParentVoice);
                
                return true;
            }
            
            // Recursively check in the subVoice's subVoices
            if (updateVoiceInSubVoices(choir, subVoice, oldVoice, newVoice)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Updates a Voice in the model.
     * 
     * @param oldVoice the Voice to replace
     * @param newVoice the new Voice
     */
    private void updateVoiceInModel(Voice oldVoice, Voice newVoice) {
        // Check each Choir for the Voice
        for (Choir choir : symphony.getChoirs()) {
            if (updateVoiceInChoir(choir, oldVoice, newVoice)) {
                return;
            }
        }
    }
    
    /**
     * Loads a Symphony (project) by name.
     * 
     * @param projectName the name of the project to load
     */
    public void loadProject(String projectName) {
        this.projectName = projectName;
        
        // Load the Symphony from the SymphonyService
        Optional<Symphony> symphonyOpt = symphonyService.getSymphony(projectName);
        
        if (symphonyOpt.isPresent()) {
            symphony = symphonyOpt.get();
            
            // Update the UI with the Symphony data
            updateUI();
        } else {
            // Handle the case where the Symphony doesn't exist
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Project not found: " + projectName);
            alert.showAndWait();
            
            // Close the stage
            stageProvider.closeStage("project-" + projectName.replaceAll("\\s+", "-").toLowerCase());
        }
    }
    
    /**
     * Handles the action when the "Add List" button is clicked.
     * Opens a dialog to enter a list name and creates a new Choir.
     */
    @FXML
    public void onAddChoir() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New List");
        dialog.setHeaderText("Create a new list");
        dialog.setContentText("Please enter the list name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(choirName -> {
            if (!choirName.trim().isEmpty()) {
                try {
                    // Create a new Choir with the given name and no description
                    Choir newChoir = new Choir(choirName, null, Collections.emptyList());
                    
                    // Add the Choir to the Symphony
                    symphony = symphonyService.addChoir(symphony.getTitle(), newChoir);
                    
                    // Update the UI
                    updateUI();
                    
                    // Show a confirmation message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("List Created");
                    alert.setHeaderText(null);
                    alert.setContentText("List '" + choirName + "' has been created successfully.");
                    alert.showAndWait();
                } catch (IllegalArgumentException e) {
                    // Show an error message if the list already exists
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to create list: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }
    
    /**
     * Updates the UI with the Symphony data.
     */
    private void updateUI() {
        // Clear maps
        itemToModelMap.clear();
        pathToItemMap.clear();
        
        // Set the project title and description
        projectTitleLabel.setText(symphony.getTitle());
        projectDescriptionLabel.setText(symphony.getDescription().orElse(""));
        
        // Clear the choirs list
        choirsList.getChildren().clear();
        
        // Add each Choir to the choirs list
        for (Choir choir : symphony.getChoirs()) {
            Button choirButton = new Button(choir.getName());
            choirButton.getStyleClass().add("choir-button");
            choirButton.setMaxWidth(Double.MAX_VALUE);
            choirButton.setOnAction(event -> {
                // Select the choir in the TreeView
                for (TreeItem<String> item : voicesTreeView.getRoot().getChildren()) {
                    if (item.getValue().equals(choir.getName())) {
                        voicesTreeView.getSelectionModel().select(item);
                        break;
                    }
                }
            });
            choirsList.getChildren().add(choirButton);
        }
        
        // Create the root item for the TreeView
        TreeItem<String> root = new TreeItem<>(symphony.getTitle());
        root.setExpanded(true);
        itemToModelMap.put(root, symphony);
        pathToItemMap.put(symphony.getTitle(), root);
        
        // Add each Choir and its Voices to the TreeView
        for (Choir choir : symphony.getChoirs()) {
            // Create a TreeItem for the Choir
            TreeItem<String> choirItem = new TreeItem<>(choir.getName());
            choirItem.setExpanded(true);
            root.getChildren().add(choirItem);
            
            // Store the mapping
            itemToModelMap.put(choirItem, choir);
            pathToItemMap.put(choir.getName(), choirItem);
            
            // Add each Voice in the Choir to the TreeView
            for (Voice voice : choir.getVoices()) {
                addVoiceToTreeItem(choirItem, voice, choir.getName());
            }
        }
        
        // Set the root item for the TreeView
        voicesTreeView.setRoot(root);
        
        // Use a custom cell factory to display checkboxes
        voicesTreeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<String>() {
                private final CheckBox checkBox = new CheckBox();
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        getStyleClass().remove("root-tree-cell");
                    } else {
                        // Set the text
                        setText(item);
                        
                        // Get the tree item and its level
                        TreeItem<String> treeItem = getTreeItem();
                        int level = 0;
                        if (treeItem != null) {
                            // Calculate the level of this tree item
                            TreeItem<String> parent = treeItem.getParent();
                            while (parent != null) {
                                level++;
                                parent = parent.getParent();
                            }
                            
                            // Add or remove the root-tree-cell style class based on level
                            if (level <= 1) {
                                // This is a root or first-level item, add the style class
                                if (!getStyleClass().contains("root-tree-cell")) {
                                    getStyleClass().add("root-tree-cell");
                                }
                            } else {
                                // This is a deeper level item, remove the style class
                                getStyleClass().remove("root-tree-cell");
                            }
                            
                            // Only show checkboxes for Voice items, not Symphony or Choir
                            if (itemToModelMap.get(treeItem) instanceof Voice) {
                                Voice voice = (Voice) itemToModelMap.get(treeItem);
                                
                                // Configure the checkbox based on the Voice's completed status
                                checkBox.setSelected(voice.isCompleted());
                                
                                // Add listener to update the model when checkbox state changes
                                checkBox.setOnAction(event -> {
                                    if (symphony != null) {
                                        boolean isChecked = checkBox.isSelected();
                                        
                                        // Create a new Voice with the updated completed status
                                        Voice updatedVoice = voice.withCompleted(isChecked);
                                        
                                        // Update the Voice in the model
                                        updateVoiceInModel(voice, updatedVoice);
                                        
                                        // Update the mapping
                                        itemToModelMap.put(treeItem, updatedVoice);
                                        
                                        // Save the updated Symphony
                                        symphony = symphonyService.updateSymphony(symphony);
                                    }
                                });
                                
                                // Create a container for the checkbox and add button
                                HBox container = new HBox(5);
                                container.setAlignment(Pos.CENTER_LEFT);
                                
                                // Add the checkbox to the container
                                container.getChildren().add(checkBox);
                                
                                // Add a '+' button to add a new Voice
                                Button addButton = new Button("+");
                                addButton.getStyleClass().add("add-voice-button");
                                addButton.setOnAction(event -> {
                                    // Open a dialog to get the new Voice title
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("New Task");
                                    dialog.setHeaderText("Create a new task");
                                    dialog.setContentText("Please enter the task title:");
                                    
                                    Optional<String> result = dialog.showAndWait();
                                    result.ifPresent(voiceTitle -> {
                                        if (!voiceTitle.trim().isEmpty()) {
                                            try {
                                                // Create a new Voice with the given title and no description
                                                Voice newVoice = new Voice(voiceTitle, null, Collections.emptyList());
                                                
                                                // Add the Voice to the parent Voice's subVoices
                                                Voice parentVoice = (Voice) itemToModelMap.get(treeItem);
                                                
                                                // Create a new list with the existing subVoices plus the new one
                                                List<Voice> updatedSubVoices = new ArrayList<>(parentVoice.getSubVoices());
                                                updatedSubVoices.add(newVoice);
                                                
                                                // Create a new parent Voice with the updated subVoices
                                                Voice updatedParentVoice = new Voice(
                                                        parentVoice.getTitle(),
                                                        parentVoice.getDescription().orElse(null),
                                                        updatedSubVoices,
                                                        parentVoice.isCompleted()
                                                );
                                                
                                                // Update the parent Voice in the model
                                                updateVoiceInModel(parentVoice, updatedParentVoice);
                                                
                                                // Save the updated Symphony
                                                symphony = symphonyService.updateSymphony(symphony);
                                                
                                                // Update the UI
                                                updateUI();
                                            } catch (IllegalArgumentException e) {
                                                // Show an error message if the Voice already exists
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Failed to create task: " + e.getMessage());
                                                alert.showAndWait();
                                            }
                                        }
                                    });
                                });
                                
                                // Add the button to the container
                                container.getChildren().add(addButton);
                                
                                // Set the graphic to the container
                                setGraphic(container);
                            } else if (itemToModelMap.get(treeItem) instanceof Choir) {
                                // For Choir items, add a '+' button to add a new Voice
                                Button addButton = new Button("+");
                                addButton.getStyleClass().add("add-voice-button");
                                addButton.setOnAction(event -> {
                                    // Open a dialog to get the new Voice title
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("New Task");
                                    dialog.setHeaderText("Create a new task");
                                    dialog.setContentText("Please enter the task title:");
                                    
                                    Optional<String> result = dialog.showAndWait();
                                    result.ifPresent(voiceTitle -> {
                                        if (!voiceTitle.trim().isEmpty()) {
                                            try {
                                                // Create a new Voice with the given title and no description
                                                Voice newVoice = new Voice(voiceTitle, null, Collections.emptyList());
                                                
                                                // Add the Voice to the Choir
                                                Choir choir = (Choir) itemToModelMap.get(treeItem);
                                                symphony = symphonyService.addVoice(symphony.getTitle(), choir.getName(), newVoice);
                                                
                                                // Update the UI
                                                updateUI();
                                            } catch (IllegalArgumentException e) {
                                                // Show an error message if the Voice already exists
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Failed to create task: " + e.getMessage());
                                                alert.showAndWait();
                                            }
                                        }
                                    });
                                });
                                
                                // Set the graphic to the button
                                setGraphic(addButton);
                            } else {
                                // No graphic for Symphony
                                setGraphic(null);
                            }
                        }
                    }
                }
            };
            
            // Add CSS class for styling indentation lines
            cell.getStyleClass().add("voice-tree-cell");
            
            return cell;
        });
    }
    
    /**
     * Recursively adds a Voice and its subVoices to a TreeItem.
     * 
     * @param parent the parent TreeItem to add the Voice to
     * @param voice the Voice to add
     * @param parentPath the path of the parent item
     */
    private void addVoiceToTreeItem(TreeItem<String> parent, Voice voice, String parentPath) {
        // Create a TreeItem for the Voice
        String path = parentPath + "/" + voice.getTitle();
        TreeItem<String> voiceItem = new TreeItem<>(voice.getTitle());
        parent.getChildren().add(voiceItem);
        
        // Store the mapping
        itemToModelMap.put(voiceItem, voice);
        pathToItemMap.put(path, voiceItem);
        
        // Add each subVoice recursively
        for (Voice subVoice : voice.getSubVoices()) {
            addVoiceToTreeItem(voiceItem, subVoice, path);
        }
    }
}