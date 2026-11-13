package gal.usc.etse.es.servicios.repository; // O el paquete donde lo tengas

import gal.usc.etse.es.servicios.service.DexSyncService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Component para que se gestione por Spring al iniciar
@Component
public class DexDataLoader implements ApplicationRunner { // Se ejecuta una única vez en el arranque del servidor

    private final DexSyncService dexSyncService;

    public DexDataLoader(DexSyncService dexSyncService) {
        this.dexSyncService = dexSyncService;
    }

    // Llama inicialmente al servicio de sincronización de pokedex
    @Override
    public void run(ApplicationArguments args) {
        // Delegamos la sincronización en el servicio
        dexSyncService.syncDex();
    }
}