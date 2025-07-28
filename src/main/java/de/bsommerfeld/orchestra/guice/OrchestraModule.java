package de.bsommerfeld.orchestra.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import de.bsommerfeld.orchestra.persistence.mapper.ChoirMapper;
import de.bsommerfeld.orchestra.persistence.mapper.ChoirMapperImpl;
import de.bsommerfeld.orchestra.persistence.mapper.SymphonyMapper;
import de.bsommerfeld.orchestra.persistence.mapper.SymphonyMapperImpl;
import de.bsommerfeld.orchestra.persistence.mapper.VoiceMapper;
import de.bsommerfeld.orchestra.persistence.mapper.VoiceMapperImpl;
import de.bsommerfeld.orchestra.persistence.repository.JsonSymphonyRepository;
import de.bsommerfeld.orchestra.persistence.repository.SymphonyRepository;
import de.bsommerfeld.orchestra.persistence.service.SymphonyService;
import de.bsommerfeld.orchestra.persistence.service.SymphonyServiceImpl;
import de.bsommerfeld.orchestra.ui.view.ViewLoader;
import de.bsommerfeld.orchestra.ui.view.ViewProvider;
import de.bsommerfeld.orchestra.ui.view.StageProvider;

/**
 * Guice module for the Orchestra application.
 * Configures dependency injection bindings.
 */
public class OrchestraModule extends AbstractModule {

    @Override
    protected void configure() {
        // UI components
        bind(ViewProvider.class).asEagerSingleton();
        bind(StageProvider.class).asEagerSingleton();
        bind(ViewLoader.class).in(Singleton.class);
        
        // Persistence components
        
        // Mappers
        bind(VoiceMapper.class).to(VoiceMapperImpl.class);
        bind(ChoirMapper.class).to(ChoirMapperImpl.class);
        bind(SymphonyMapper.class).to(SymphonyMapperImpl.class);
        
        // Repositories
        bind(SymphonyRepository.class).to(JsonSymphonyRepository.class);
        
        // Services
        bind(SymphonyService.class).to(SymphonyServiceImpl.class);
    }
}
