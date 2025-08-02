package de.bsommerfeld.orchestra.ui.controller;

import de.bsommerfeld.orchestra.ui.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure the scroll pane for better user experience
        taskScrollPane.setPannable(true);
        taskScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        taskScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Führe das Zeichnen aus, nachdem das UI-Layout vollständig berechnet wurde.
        Platform.runLater(() -> {
            setupDraggableCards();
            drawConnections();
        });
    }
    
    private void setupDraggableCards() {
        // Ensure connections pane is always on top of the task canvas but below the cards
        connectionsPane.setViewOrder(1.0);
        
        List<HBox> cards = Arrays.asList(rootTask, subtask1, subtask2, subtask3, subtask4, subSubtask1, subSubtask2);
        
        for (HBox card : cards) {
            // Set view order to ensure cards are above the connections pane
            card.setViewOrder(0.5);
            
            // Setup mouse pressed event
            card.setOnMousePressed(this::handleCardPressed);
            
            // Setup mouse dragged event
            card.setOnMouseDragged(this::handleCardDragged);
            
            // Setup mouse released event
            card.setOnMouseReleased(this::handleCardReleased);
        }
    }
    
    private void handleCardPressed(MouseEvent event) {
        // Store the initial mouse position relative to the card
        HBox card = (HBox) event.getSource();
        dragOffsetX = event.getSceneX() - card.getLayoutX();
        dragOffsetY = event.getSceneY() - card.getLayoutY();
        
        // Bring the card to front
        card.toFront();
        
        // Consume the event to prevent it from bubbling up
        event.consume();
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
        connectNodes(rootTask, subtask1);
        connectNodes(rootTask, subtask2);
        connectNodes(rootTask, subtask3);
        connectNodes(rootTask, subtask4);
        connectNodes(subtask3, subSubtask1);
        connectNodes(subtask4, subSubtask2);
    }

    private void connectNodes(Node startNode, Node endNode) {
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
        
        // Style the curve
        curve.setStroke(Color.web("#555555"));
        curve.setStrokeWidth(2);
        curve.setFill(null);
        
        // Add the curve to the connections pane
        connectionsPane.getChildren().add(curve);
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        Node sourceButton = (Node) event.getSource();
        HBox parentCard = (HBox) sourceButton.getParent();
        System.out.println("Der 'Add'-Button der Karte mit der ID '" + parentCard.getId() + "' wurde geklickt.");
    }
}