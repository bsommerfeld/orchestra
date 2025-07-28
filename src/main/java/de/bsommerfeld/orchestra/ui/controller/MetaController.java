package de.bsommerfeld.orchestra.ui.controller;

import com.google.inject.Inject;
import de.bsommerfeld.orchestra.ui.view.StageProvider;
import de.bsommerfeld.orchestra.ui.view.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

@View
public class MetaController {

    private final StageProvider stageProvider;

    @Inject
    public MetaController(StageProvider stageProvider) {
        this.stageProvider = stageProvider;
    }

    @FXML
    public void onAction(ActionEvent actionEvent) {
        stageProvider.createStage("test");
        stageProvider.showView("test", MetaController.class, "titel");
    }
}
