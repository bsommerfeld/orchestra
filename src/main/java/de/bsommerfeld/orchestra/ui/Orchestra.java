package de.bsommerfeld.orchestra.ui;

import atlantafx.base.theme.PrimerLight;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.bsommerfeld.orchestra.guice.OrchestraModule;
import javafx.application.Application;
import javafx.stage.Stage;

public class Orchestra extends Application {

    private final Injector injector;

    public Orchestra() {
        this.injector = Guice.createInjector(new OrchestraModule());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        stage.show();
    }
}
