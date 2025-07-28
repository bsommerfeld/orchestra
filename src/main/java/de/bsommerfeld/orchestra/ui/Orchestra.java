package de.bsommerfeld.orchestra.ui;

import atlantafx.base.theme.PrimerLight;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.bsommerfeld.orchestra.guice.OrchestraModule;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Orchestra extends Application {

    private final Injector injector;

    private double xOffset = 0;
    private double yOffset = 0;

    public Orchestra() {
        this.injector = Guice.createInjector(new OrchestraModule());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        VBox root = new VBox();

        HBox customTitleBar = new HBox();
        customTitleBar.getStyleClass().add("custom-title-bar");
        customTitleBar.setAlignment(Pos.CENTER_LEFT);

        Label projectTitle = new Label("Projekt XYZ");
        projectTitle.getStyleClass().add("project-title-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        customTitleBar.getChildren().addAll(projectTitle, spacer);

        // NEU: Maus-Event-Handler für customTitleBar hinzufügen
        customTitleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        customTitleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        StackPane mainContent = new StackPane(new Label("Your main application content (Kanban Board, etc.)"));
        mainContent.getStyleClass().add("main-content");
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        root.getChildren().addAll(customTitleBar, mainContent);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/orchestra-styles.css").toExternalForm());

        stage.setTitle("Orchestra");
        stage.initStyle(StageStyle.EXTENDED);
        stage.setScene(scene);
        stage.show();
    }
}
