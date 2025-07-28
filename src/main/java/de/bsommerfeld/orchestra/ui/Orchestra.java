package de.bsommerfeld.orchestra.ui;

import atlantafx.base.theme.PrimerLight;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.bsommerfeld.orchestra.guice.OrchestraModule;
import de.bsommerfeld.orchestra.ui.controller.MetaController;
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
        Parent root = viewProvider.requestView(MetaController.class).parent();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Orchestra");
        stage.show();
    }
}
