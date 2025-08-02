package de.bsommerfeld.orchestra.ui.controller;

import de.bsommerfeld.orchestra.ui.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

import java.net.URL;
import java.util.ResourceBundle;

@View
public class TaskController implements Initializable {

    @FXML
    private Pane connectionsPane;
    @FXML
    private HBox rootTask, subtask1, subtask2, subtask3, subtask4, subSubtask1, subSubtask2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Führe das Zeichnen aus, nachdem das UI-Layout vollständig berechnet wurde.
        Platform.runLater(this::drawConnections);
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
        Bounds startBounds = startNode.getBoundsInParent();
        Bounds endBounds = endNode.getBoundsInParent();

        double startX = startBounds.getMaxX();
        double startY = startBounds.getMinY() + startBounds.getHeight() / 2;
        double endX = endBounds.getMinX();
        double endY = endBounds.getMinY() + endBounds.getHeight() / 2;

        double controlOffsetX = Math.abs(endX - startX) * 0.5;
        CubicCurve curve = new CubicCurve(
            startX, startY,                  // start point
            startX + controlOffsetX, startY, // control point 1
            endX - controlOffsetX, endY,     // control point 2
            endX, endY                       // end point
        );
        curve.setStroke(Color.web("#555555"));
        curve.setStrokeWidth(2);
        curve.setFill(null);

        connectionsPane.getChildren().add(curve);
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        Node sourceButton = (Node) event.getSource();
        HBox parentCard = (HBox) sourceButton.getParent();
        System.out.println("Der 'Add'-Button der Karte mit der ID '" + parentCard.getId() + "' wurde geklickt.");
    }
}