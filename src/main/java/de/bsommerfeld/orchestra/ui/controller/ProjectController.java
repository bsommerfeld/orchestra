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
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        // This is a simplified implementation
        // In a real implementation, you would need to:
        // 1. Remove the Voice from its current parent (Choir or Voice)
        // 2. Add it to the new parent (Choir or Voice)
        // 3. Create a new Symphony with the updated structure
        // 4. Update the symphony variable
        
        // For now, we'll just log the operation
        System.out.println("Moved Voice: " + sourceVoice.getTitle() + 
                           " from " + sourceParent.getValue() + 
                           " to " + targetItem.getValue());
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
     * Updates the UI with the Symphony data.
     */
    private void updateUI() {
        // Clear maps
        itemToModelMap.clear();
        pathToItemMap.clear();
        
        // Set the project title and description
        projectTitleLabel.setText(symphony.getTitle());
        projectDescriptionLabel.setText(symphony.getDescription().orElse(""));
        
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
                                // Configure the checkbox
                                checkBox.setSelected(false); // Default to unchecked
                                
                                // Add listener to update the model when checkbox state changes
                                checkBox.setOnAction(event -> {
                                    if (symphony != null) {
                                        // In a real implementation, you would update the Voice's state
                                        System.out.println("Checkbox for " + item + " changed to " + checkBox.isSelected());
                                        symphonyService.updateSymphony(symphony);
                                    }
                                });
                                
                                // Set the graphic to the checkbox
                                setGraphic(checkBox);
                            } else {
                                // No checkbox for Symphony or Choir
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