package de.bsommerfeld.orchestra.ui.controller;

import de.bsommerfeld.orchestra.ui.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@View
public class TaskController implements Initializable {

    @FXML
    private Pane connectionsPane;
    @FXML
    private ScrollPane taskScrollPane;
    @FXML
    private AnchorPane taskCanvas;
    @FXML
    private HBox rootTask, subtask1, subtask2, subtask3, subtask4, subSubtask1, subSubtask2;
    
    // Variables for drag functionality
    private double dragOffsetX, dragOffsetY;
    
    // Variables for canvas dragging
    private double canvasDragStartX, canvasDragStartY;
    
    // Data structures for tracking task relationships and layout
    private Map<String, HBox> taskCards = new HashMap<>();
    private Map<String, List<String>> taskRelationships = new HashMap<>();
    private Map<String, Integer> taskLevels = new HashMap<>();
    private Map<Integer, List<String>> levelToTasks = new HashMap<>();
    private Map<String, CheckBox> taskCheckboxes = new HashMap<>();
    
    // Variable to track the currently selected task
    private String selectedTaskId = null;
    
    // Layout constants
    private static final double HORIZONTAL_SPACING = 250.0;
    private static final double VERTICAL_SPACING = 100.0;
    private static final double INITIAL_LEFT_MARGIN = 50.0;
    private static final double INITIAL_TOP_MARGIN = 50.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure the scroll pane for better user experience
        taskScrollPane.setPannable(true);
        taskScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        taskScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Add a listener to handle window resizing
        taskScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            // When the viewport size changes, update the canvas size
            Platform.runLater(this::updateCanvasSize);
        });
        
        // Execute after the UI layout has been fully calculated
        Platform.runLater(() -> {
            // Initialize task relationships
            initializeTaskRelationships();
            
            // Setup draggable cards and register them in our data structures
            setupDraggableCards();
            
            // Setup canvas dragging
            setupCanvasDragging();
            
            // Apply automatic layout
            applyAutomaticLayout();
            
            // Update canvas size and draw connections
            updateCanvasSize();
            drawConnections();
        });
    }
    
    private void initializeTaskRelationships() {
        // Clear the task canvas
        taskCanvas.getChildren().clear();
        taskCanvas.getChildren().add(connectionsPane);
        
        // Create new task cards with the updated structure
        HBox newRootTask = createTaskCard("rootTask", "Main Task", "High Priority");
        HBox newSubtask1 = createTaskCard("subtask1", "Subtask 1", "Medium Priority");
        HBox newSubtask2 = createTaskCard("subtask2", "Subtask 2", "Low Priority");
        HBox newSubtask3 = createTaskCard("subtask3", "Subtask 3", "In Progress");
        HBox newSubtask4 = createTaskCard("subtask4", "Subtask 4", "Pending Review");
        HBox newSubSubtask1 = createTaskCard("subSubtask1", "Sub-subtask 1", "Almost Done");
        HBox newSubSubtask2 = createTaskCard("subSubtask2", "Sub-subtask 2", "Just Started");
        
        // Position the task cards on the canvas
        AnchorPane.setLeftAnchor(newRootTask, 50.0);
        AnchorPane.setTopAnchor(newRootTask, 250.0);
        
        AnchorPane.setLeftAnchor(newSubtask1, 350.0);
        AnchorPane.setTopAnchor(newSubtask1, 80.0);
        
        AnchorPane.setLeftAnchor(newSubtask2, 350.0);
        AnchorPane.setTopAnchor(newSubtask2, 160.0);
        
        AnchorPane.setLeftAnchor(newSubtask3, 350.0);
        AnchorPane.setTopAnchor(newSubtask3, 260.0);
        
        AnchorPane.setLeftAnchor(newSubtask4, 350.0);
        AnchorPane.setTopAnchor(newSubtask4, 360.0);
        
        AnchorPane.setLeftAnchor(newSubSubtask1, 600.0);
        AnchorPane.setTopAnchor(newSubSubtask1, 220.0);
        
        AnchorPane.setLeftAnchor(newSubSubtask2, 600.0);
        AnchorPane.setTopAnchor(newSubSubtask2, 400.0);
        
        // Add the task cards to the canvas
        taskCanvas.getChildren().addAll(
            newRootTask, newSubtask1, newSubtask2, newSubtask3, 
            newSubtask4, newSubSubtask1, newSubSubtask2
        );
        
        // Register all task cards
        taskCards.put("rootTask", newRootTask);
        taskCards.put("subtask1", newSubtask1);
        taskCards.put("subtask2", newSubtask2);
        taskCards.put("subtask3", newSubtask3);
        taskCards.put("subtask4", newSubtask4);
        taskCards.put("subSubtask1", newSubSubtask1);
        taskCards.put("subSubtask2", newSubSubtask2);
        
        // Define task relationships (parent -> children)
        taskRelationships.put("rootTask", new ArrayList<>(Arrays.asList("subtask1", "subtask2", "subtask3", "subtask4")));
        taskRelationships.put("subtask3", new ArrayList<>(Arrays.asList("subSubtask1")));
        taskRelationships.put("subtask4", new ArrayList<>(Arrays.asList("subSubtask2")));
        
        // Initialize empty lists for tasks without children
        taskRelationships.putIfAbsent("subtask1", new ArrayList<>());
        taskRelationships.putIfAbsent("subtask2", new ArrayList<>());
        taskRelationships.putIfAbsent("subSubtask1", new ArrayList<>());
        taskRelationships.putIfAbsent("subSubtask2", new ArrayList<>());
        
        // Calculate task levels (depth in the hierarchy)
        calculateTaskLevels();
    }
    
    private void calculateTaskLevels() {
        // Clear existing levels
        taskLevels.clear();
        levelToTasks.clear();
        
        // Set root task at level 0
        taskLevels.put("rootTask", 0);
        levelToTasks.put(0, new ArrayList<>(Arrays.asList("rootTask")));
        
        // Calculate levels for all other tasks
        calculateChildLevels("rootTask", 0);
    }
    
    private void calculateChildLevels(String parentId, int parentLevel) {
        List<String> children = taskRelationships.get(parentId);
        if (children == null || children.isEmpty()) {
            return;
        }
        
        int childLevel = parentLevel + 1;
        
        // Ensure the level exists in the map
        levelToTasks.putIfAbsent(childLevel, new ArrayList<>());
        
        for (String childId : children) {
            // Set the child's level
            taskLevels.put(childId, childLevel);
            
            // Add the child to the level's task list
            levelToTasks.get(childLevel).add(childId);
            
            // Recursively calculate levels for this child's children
            calculateChildLevels(childId, childLevel);
        }
    }
    
    private void applyAutomaticLayout() {
        // Get the maximum level
        int maxLevel = 0;
        for (int level : levelToTasks.keySet()) {
            if (level > maxLevel) {
                maxLevel = level;
            }
        }
        
        // Calculate the total width of the hierarchy
        double totalHierarchyWidth = maxLevel * HORIZONTAL_SPACING;
        
        // Calculate the center position of the canvas
        double canvasWidth = taskCanvas.getWidth();
        if (canvasWidth == 0) {
            // If the canvas width is not yet available, use the initial size from FXML
            canvasWidth = taskCanvas.getPrefWidth();
        }
        
        // Calculate the starting X position to center the hierarchy
        double startX = Math.max((canvasWidth - totalHierarchyWidth) / 2, INITIAL_LEFT_MARGIN);
        
        // Apply layout level by level
        for (int level = 0; level <= maxLevel; level++) {
            List<String> tasksInLevel = levelToTasks.get(level);
            if (tasksInLevel == null || tasksInLevel.isEmpty()) {
                continue;
            }
            
            // Calculate horizontal position for this level with centering
            double levelX = startX + (level * HORIZONTAL_SPACING);
            
            // Calculate total height of tasks in this level
            double totalLevelHeight = 0;
            for (String taskId : tasksInLevel) {
                HBox taskCard = taskCards.get(taskId);
                if (taskCard != null) {
                    totalLevelHeight += taskCard.getPrefHeight() + VERTICAL_SPACING;
                }
            }
            // Subtract the last vertical spacing
            if (!tasksInLevel.isEmpty()) {
                totalLevelHeight -= VERTICAL_SPACING;
            }
            
            // Calculate starting Y position to center tasks vertically
            double canvasHeight = taskCanvas.getHeight();
            if (canvasHeight == 0) {
                canvasHeight = taskCanvas.getPrefHeight();
            }
            double startY = Math.max((canvasHeight - totalLevelHeight) / 2, INITIAL_TOP_MARGIN);
            
            // Position each task in this level
            double currentY = startY;
            for (String taskId : tasksInLevel) {
                HBox taskCard = taskCards.get(taskId);
                if (taskCard != null) {
                    // Clear any existing constraints
                    AnchorPane.clearConstraints(taskCard);
                    
                    // Set the position
                    taskCard.setLayoutX(levelX);
                    taskCard.setLayoutY(currentY);
                    
                    // Update Y for the next task in this level
                    currentY += taskCard.getPrefHeight() + VERTICAL_SPACING;
                }
            }
        }
    }
    
    private void setupCanvasDragging() {
        // Setup mouse events for dragging the canvas background
        taskCanvas.setOnMousePressed(this::handleCanvasPressed);
        taskCanvas.setOnMouseDragged(this::handleCanvasDragged);
        taskCanvas.setOnMouseReleased(this::handleCanvasReleased);
    }
    
    private void handleCanvasPressed(MouseEvent event) {
        // Only handle events directly on the canvas, not on its children
        if (event.getTarget() != taskCanvas) {
            return;
        }
        
        // Store the initial scroll values and mouse position
        canvasDragStartX = event.getX();
        canvasDragStartY = event.getY();
        
        // Deselect the current task when clicking on the canvas
        if (selectedTaskId != null) {
            selectedTaskId = null;
            updateTaskSelection();
        }
        
        // Consume the event to prevent it from bubbling up
        event.consume();
    }
    
    private void handleCanvasDragged(MouseEvent event) {
        // Only handle events directly on the canvas, not on its children
        if (event.getTarget() != taskCanvas) {
            return;
        }
        
        // Calculate the drag distance
        double dragX = canvasDragStartX - event.getX();
        double dragY = canvasDragStartY - event.getY();
        
        // Calculate the content size and viewport size
        double contentWidth = taskCanvas.getWidth();
        double contentHeight = taskCanvas.getHeight();
        double viewportWidth = taskScrollPane.getViewportBounds().getWidth();
        double viewportHeight = taskScrollPane.getViewportBounds().getHeight();
        
        // Calculate the new scroll values
        double newHvalue = taskScrollPane.getHvalue() + (dragX / (contentWidth - viewportWidth));
        double newVvalue = taskScrollPane.getVvalue() + (dragY / (contentHeight - viewportHeight));
        
        // Clamp the values between 0 and 1
        newHvalue = Math.max(0, Math.min(1, newHvalue));
        newVvalue = Math.max(0, Math.min(1, newVvalue));
        
        // Update the scroll position
        taskScrollPane.setHvalue(newHvalue);
        taskScrollPane.setVvalue(newVvalue);
        
        // Update the start position for the next drag event
        canvasDragStartX = event.getX();
        canvasDragStartY = event.getY();
        
        // Consume the event
        event.consume();
    }
    
    private void handleCanvasReleased(MouseEvent event) {
        // Only handle events directly on the canvas, not on its children
        if (event.getTarget() != taskCanvas) {
            return;
        }
        
        // Consume the event
        event.consume();
    }
    
    private void updateCanvasSize() {
        // Find the rightmost and bottommost positions of all task cards
        double maxX = 0;
        double maxY = 0;
        
        for (HBox card : taskCards.values()) {
            Bounds bounds = card.getBoundsInParent();
            double rightEdge = bounds.getMaxX();
            double bottomEdge = bounds.getMaxY();
            
            if (rightEdge > maxX) {
                maxX = rightEdge;
            }
            
            if (bottomEdge > maxY) {
                maxY = bottomEdge;
            }
        }
        
        // Add some padding to ensure there's space for new tasks
        double padding = 200;
        
        // Ensure the canvas is at least as large as the viewport
        double viewportWidth = taskScrollPane.getViewportBounds().getWidth();
        double viewportHeight = taskScrollPane.getViewportBounds().getHeight();
        
        // Calculate new dimensions
        double newWidth = Math.max(maxX + padding, viewportWidth);
        double newHeight = Math.max(maxY + padding, viewportHeight);
        
        // Update the canvas size
        taskCanvas.setPrefWidth(newWidth);
        taskCanvas.setPrefHeight(newHeight);
        
        // Log the new canvas size
        System.out.println("Canvas resized to: " + newWidth + "x" + newHeight);
    }
    
    private void setupDraggableCards() {
        // Ensure connections pane is always on top of the task canvas but below the cards
        connectionsPane.setViewOrder(1.0);
        
        // Make all existing cards draggable
        for (HBox card : taskCards.values()) {
            setupDraggableCard(card);
        }
    }
    
    private void handleCardPressed(MouseEvent event) {
        // Store the initial mouse position relative to the card
        HBox card = (HBox) event.getSource();
        dragOffsetX = event.getSceneX() - card.getLayoutX();
        dragOffsetY = event.getSceneY() - card.getLayoutY();
        
        // Bring the card to front
        card.toFront();
        
        // Set this card as the selected task if it's a left-click
        if (event.getButton() == MouseButton.PRIMARY) {
            String taskId = card.getId();
            if (!taskId.equals(selectedTaskId)) {
                selectedTaskId = taskId;
                updateTaskSelection();
            }
        }
        
        // Consume the event to prevent it from bubbling up
        event.consume();
    }
    
    /**
     * Updates the styling of all tasks and connections based on the selected task.
     */
    private void updateTaskSelection() {
        if (selectedTaskId == null) {
            // If no task is selected, reset all tasks and connections to normal
            for (HBox card : taskCards.values()) {
                card.getStyleClass().remove("task-card-selected");
                card.getStyleClass().remove("task-card-dimmed");
                card.setOpacity(1.0);
            }
            // Redraw connections with normal styling
            drawConnections();
            return;
        }
        
        // Get all tasks directly connected to the selected task
        List<String> connectedTasks = new ArrayList<>();
        
        // Add children of selected task
        List<String> children = taskRelationships.get(selectedTaskId);
        if (children != null) {
            connectedTasks.addAll(children);
        }
        
        // Add parents of selected task
        for (Map.Entry<String, List<String>> entry : taskRelationships.entrySet()) {
            if (entry.getValue().contains(selectedTaskId)) {
                connectedTasks.add(entry.getKey());
            }
        }
        
        // Update styling for all task cards
        for (Map.Entry<String, HBox> entry : taskCards.entrySet()) {
            String taskId = entry.getKey();
            HBox card = entry.getValue();
            
            // Remove existing selection-related style classes
            card.getStyleClass().remove("task-card-selected");
            card.getStyleClass().remove("task-card-dimmed");
            
            if (taskId.equals(selectedTaskId)) {
                // Selected task gets highlighted
                card.getStyleClass().add("task-card-selected");
                card.setOpacity(1.0);
            } else if (!connectedTasks.contains(taskId)) {
                // Tasks not connected to the selected task get dimmed
                card.getStyleClass().add("task-card-dimmed");
            } else {
                // Connected tasks remain at full opacity
                card.setOpacity(1.0);
            }
        }
        
        // Redraw connections with updated styling
        drawConnections();
    }
    
    private void handleCardDragged(MouseEvent event) {
        HBox card = (HBox) event.getSource();
        
        // Calculate new position
        double newX = event.getSceneX() - dragOffsetX;
        double newY = event.getSceneY() - dragOffsetY;
        
        // Ensure the card stays within the canvas bounds
        newX = Math.max(0, Math.min(newX, taskCanvas.getWidth() - card.getWidth()));
        newY = Math.max(0, Math.min(newY, taskCanvas.getHeight() - card.getHeight()));
        
        // Update card position
        card.setLayoutX(newX);
        card.setLayoutY(newY);
        
        // Remove any anchor pane constraints
        AnchorPane.clearConstraints(card);
        
        // Redraw connections
        drawConnections();
        
        // Update canvas size if needed
        updateCanvasSize();
        
        // Ensure the scroll pane shows the area where the card is being dragged
        ensureCardVisible(card);
        
        // Consume the event
        event.consume();
    }
    
    private void ensureCardVisible(HBox card) {
        // Calculate the card's bounds in the scroll pane's coordinate system
        Bounds cardBounds = card.getBoundsInParent();
        
        // Get the current viewport position
        double viewportWidth = taskScrollPane.getViewportBounds().getWidth();
        double viewportHeight = taskScrollPane.getViewportBounds().getHeight();
        double scrollH = taskScrollPane.getHvalue();
        double scrollV = taskScrollPane.getVvalue();
        
        // Calculate the content size
        double contentWidth = taskCanvas.getWidth();
        double contentHeight = taskCanvas.getHeight();
        
        // Calculate the visible area
        double visibleLeft = scrollH * (contentWidth - viewportWidth);
        double visibleTop = scrollV * (contentHeight - viewportHeight);
        double visibleRight = visibleLeft + viewportWidth;
        double visibleBottom = visibleTop + viewportHeight;
        
        // Check if the card is outside the visible area
        if (cardBounds.getMinX() < visibleLeft) {
            // Scroll left
            taskScrollPane.setHvalue((cardBounds.getMinX()) / (contentWidth - viewportWidth));
        } else if (cardBounds.getMaxX() > visibleRight) {
            // Scroll right
            taskScrollPane.setHvalue((cardBounds.getMaxX() - viewportWidth) / (contentWidth - viewportWidth));
        }
        
        if (cardBounds.getMinY() < visibleTop) {
            // Scroll up
            taskScrollPane.setVvalue((cardBounds.getMinY()) / (contentHeight - viewportHeight));
        } else if (cardBounds.getMaxY() > visibleBottom) {
            // Scroll down
            taskScrollPane.setVvalue((cardBounds.getMaxY() - viewportHeight) / (contentHeight - viewportHeight));
        }
    }
    
    private void handleCardReleased(MouseEvent event) {
        // Redraw connections one final time
        drawConnections();
        event.consume();
    }

    private void drawConnections() {
        connectionsPane.getChildren().clear();
        
        // Draw connections based on task relationships
        for (Map.Entry<String, List<String>> entry : taskRelationships.entrySet()) {
            String parentId = entry.getKey();
            List<String> childrenIds = entry.getValue();
            
            HBox parentCard = taskCards.get(parentId);
            if (parentCard == null || childrenIds == null || childrenIds.isEmpty()) {
                continue;
            }
            
            for (String childId : childrenIds) {
                HBox childCard = taskCards.get(childId);
                if (childCard != null) {
                    boolean isSelectedConnection = false;
                    
                    // Check if this connection is related to the selected task
                    if (selectedTaskId != null) {
                        isSelectedConnection = parentId.equals(selectedTaskId) || childId.equals(selectedTaskId);
                    }
                    
                    connectNodes(parentCard, childCard, isSelectedConnection);
                }
            }
        }
    }

    private void connectNodes(Node startNode, Node endNode, boolean isSelected) {
        // Get bounds in parent coordinates to account for any transformations
        Bounds startBounds = startNode.getBoundsInParent();
        Bounds endBounds = endNode.getBoundsInParent();

        // Calculate connection points at the middle of the right side of the start node
        // and the middle of the left side of the end node
        double startX = startBounds.getMaxX();
        double startY = startBounds.getMinY() + startBounds.getHeight() / 2;
        double endX = endBounds.getMinX();
        double endY = endBounds.getMinY() + endBounds.getHeight() / 2;

        // Adjust control points based on the distance between nodes
        double distance = Math.abs(endX - startX);
        double controlOffsetX = Math.min(distance * 0.5, 100); // Limit control point distance
        
        // Create a cubic curve that connects the nodes
        CubicCurve curve = new CubicCurve(
            startX, startY,                  // start point
            startX + controlOffsetX, startY, // control point 1
            endX - controlOffsetX, endY,     // control point 2
            endX, endY                       // end point
        );
        
        // Style the curve based on selection status
        if (isSelected) {
            // Selected connection gets purple color and full opacity
            curve.setStroke(Color.web("#BA68C8"));
            curve.setStrokeWidth(2);
            curve.setOpacity(1.0);
            curve.getStyleClass().add("connection-selected");
        } else if (selectedTaskId != null) {
            // Non-selected connections get dimmed when a task is selected
            curve.setStroke(Color.web("#555555"));
            curve.setStrokeWidth(2);
            curve.setOpacity(0.5);
            curve.getStyleClass().add("connection-dimmed");
        } else {
            // Default styling when no task is selected
            curve.setStroke(Color.web("#555555"));
            curve.setStrokeWidth(2);
            curve.setOpacity(1.0);
        }
        
        curve.setFill(null);
        
        // Add the curve to the connections pane
        connectionsPane.getChildren().add(curve);
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        // Get the parent card that contains the clicked button
        Node sourceButton = (Node) event.getSource();
        HBox parentCard = (HBox) sourceButton.getParent();
        String parentId = parentCard.getId();
        
        // Generate a unique ID for the new task
        String newTaskId = "task_" + System.currentTimeMillis();
        
        // Create a new task card
        HBox newTaskCard = createTaskCard(newTaskId, "New Task", "Added " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        
        // Add the new task card to the canvas
        taskCanvas.getChildren().add(newTaskCard);
        
        // Register the new task card
        taskCards.put(newTaskId, newTaskCard);
        
        // Update task relationships
        taskRelationships.get(parentId).add(newTaskId);
        taskRelationships.put(newTaskId, new ArrayList<>());
        
        // Recalculate task levels
        calculateTaskLevels();
        
        // Apply automatic layout
        applyAutomaticLayout();
        
        // Update canvas size
        updateCanvasSize();
        
        // Redraw connections
        drawConnections();
        
        // Make the new task card draggable
        setupDraggableCard(newTaskCard);
        
        // Ensure the new card is visible
        ensureCardVisible(newTaskCard);
        
        System.out.println("Added new task '" + newTaskId + "' as child of '" + parentId + "'");
    }
    
    private HBox createTaskCard(String id, String name, String detail) {
        // Create the task card container
        HBox taskCard = new HBox();
        taskCard.setId(id);
        taskCard.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        taskCard.setSpacing(10);
        taskCard.getStyleClass().add("task-card");
        taskCard.setPadding(new Insets(8, 8, 8, 12));
        
        // Create the checkbox
        CheckBox checkbox = new CheckBox();
        checkbox.setOnAction(event -> handleTaskCheckboxAction(id, checkbox.isSelected()));
        taskCheckboxes.put(id, checkbox);
        
        // Create the content container
        VBox content = new VBox();
        content.setSpacing(5);
        
        // Create the task name label
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("task-name");
        content.getChildren().add(nameLabel);
        
        // Create the labels HBox
        HBox labelsBox = new HBox();
        labelsBox.setSpacing(5);
        labelsBox.getStyleClass().add("task-labels");
        
        // Add sample labels with different colors (in a real app, these would be dynamic)
        // Randomly select 1-3 labels with different colors
        String[] labelTexts = {"Task", "Feature", "Bug", "UI", "API", "Docs"};
        String[] labelColors = {"label-red", "label-blue", "label-green", "label-purple", "label-yellow"};
        
        int numLabels = 1 + (int)(Math.random() * 2); // 1-3 labels
        for (int i = 0; i < numLabels; i++) {
            String labelText = labelTexts[(int)(Math.random() * labelTexts.length)];
            String labelColor = labelColors[(int)(Math.random() * labelColors.length)];
            
            Label label = new Label(labelText);
            label.getStyleClass().addAll("task-label", labelColor);
            labelsBox.getChildren().add(label);
        }
        
        // Add the labels HBox to the content
        content.getChildren().add(labelsBox);
        
        // Create the add button
        Button addButton = new Button("+");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(this::handleAddTask);
        
        // Add components to the task card
        taskCard.getChildren().addAll(checkbox, content, addButton);
        
        return taskCard;
    }
    
    private void setupDraggableCard(HBox card) {
        // Set view order to ensure card is above the connections pane
        card.setViewOrder(0.5);
        
        // Setup mouse pressed event
        card.setOnMousePressed(event -> {
            // Handle right-click for context menu
            if (event.getButton() == MouseButton.SECONDARY) {
                showContextMenu(card, event);
            } else {
                // Handle normal left-click for dragging
                handleCardPressed(event);
            }
        });
        
        // Setup mouse dragged event
        card.setOnMouseDragged(this::handleCardDragged);
        
        // Setup mouse released event
        card.setOnMouseReleased(this::handleCardReleased);
    }
    
    /**
     * Shows a context menu for the task card with options to remove the task.
     * 
     * @param card The task card to show the context menu for
     * @param event The mouse event that triggered the context menu
     */
    private void showContextMenu(HBox card, MouseEvent event) {
        // Create a context menu
        ContextMenu contextMenu = new ContextMenu();
        
        // Create a menu item for removing the task
        MenuItem removeItem = new MenuItem("Remove Task");
        removeItem.setOnAction(e -> removeTask(card.getId()));
        
        // Add the menu item to the context menu
        contextMenu.getItems().add(removeItem);
        
        // Show the context menu at the mouse position
        contextMenu.show(card, event.getScreenX(), event.getScreenY());
        
        // Consume the event to prevent it from bubbling up
        event.consume();
    }
    
    /**
     * Handles the action when a task checkbox is clicked.
     * If a task is checked, all its subtasks will be checked too.
     * 
     * @param taskId The ID of the task whose checkbox was clicked
     * @param isChecked Whether the checkbox is now checked or unchecked
     */
    private void handleTaskCheckboxAction(String taskId, boolean isChecked) {
        // Update all subtasks recursively
        updateSubtaskCheckboxes(taskId, isChecked);
    }
    
    /**
     * Recursively updates the checkbox state of all subtasks.
     * 
     * @param parentId The ID of the parent task
     * @param isChecked Whether the checkboxes should be checked or unchecked
     */
    private void updateSubtaskCheckboxes(String parentId, boolean isChecked) {
        // Get the list of subtasks
        List<String> subtaskIds = taskRelationships.get(parentId);
        if (subtaskIds == null || subtaskIds.isEmpty()) {
            return;
        }
        
        // Update each subtask
        for (String subtaskId : subtaskIds) {
            // Get the checkbox for this subtask
            CheckBox checkbox = taskCheckboxes.get(subtaskId);
            if (checkbox != null) {
                // Set the checkbox state without triggering the action event
                checkbox.setSelected(isChecked);
            }
            
            // Recursively update this subtask's subtasks
            updateSubtaskCheckboxes(subtaskId, isChecked);
        }
    }
    
    /**
     * Removes a task and all its subtasks recursively.
     * 
     * @param taskId The ID of the task to remove
     */
    private void removeTask(String taskId) {
        // Get the task card
        HBox taskCard = taskCards.get(taskId);
        if (taskCard == null) {
            return;
        }
        
        // Get the list of subtasks
        List<String> subtaskIds = taskRelationships.get(taskId);
        if (subtaskIds != null) {
            // Create a copy of the list to avoid concurrent modification
            List<String> subtaskIdsCopy = new ArrayList<>(subtaskIds);
            
            // Remove all subtasks recursively
            for (String subtaskId : subtaskIdsCopy) {
                removeTask(subtaskId);
            }
        }
        
        // Remove the task from its parent's children list
        for (Map.Entry<String, List<String>> entry : taskRelationships.entrySet()) {
            entry.getValue().remove(taskId);
        }
        
        // Remove the task from our data structures
        taskCards.remove(taskId);
        taskRelationships.remove(taskId);
        taskLevels.remove(taskId);
        
        // Remove the task card from the canvas
        taskCanvas.getChildren().remove(taskCard);
        
        // Recalculate task levels
        calculateTaskLevels();
        
        // Apply automatic layout
        applyAutomaticLayout();
        
        // Update canvas size
        updateCanvasSize();
        
        // Redraw connections
        drawConnections();
        
        System.out.println("Removed task '" + taskId + "' and all its subtasks");
    }
}