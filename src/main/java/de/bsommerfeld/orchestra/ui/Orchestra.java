package de.bsommerfeld.orchestra.ui;

import atlantafx.base.theme.PrimerLight;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.bsommerfeld.orchestra.guice.OrchestraModule;
import de.bsommerfeld.orchestra.ui.controller.ProjectSelectionController;
import de.bsommerfeld.orchestra.ui.view.StageProvider;
import de.bsommerfeld.orchestra.ui.view.ViewProvider;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Orchestra extends Application {

    private final Injector injector;

    public Orchestra() {
        this.injector = Guice.createInjector(new OrchestraModule());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        ViewProvider viewProvider = injector.getInstance(ViewProvider.class);
        StageProvider stageProvider = injector.getInstance(StageProvider.class);

        // Set the primary stage in the StageProvider
        stageProvider.setPrimaryStage(stage);

        // Show the project selection view in the primary stage
        Parent root = viewProvider.requestView(ProjectSelectionController.class).parent();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Orchestra - Project Selection");
        stage.show();

        // Alternative approach using StageProvider:
        // stageProvider.showView("primary", ProjectSelectionController.class, "Orchestra - Project Selection");
    }
}
