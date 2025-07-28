package de.bsommerfeld.orchestra.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.ui.view.ViewLoader;
import de.bsommerfeld.orchestra.ui.view.ViewProvider;

public class OrchestraModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ViewProvider.class).asEagerSingleton();
        bind(ViewLoader.class).in(Singleton.class);
    }
}
