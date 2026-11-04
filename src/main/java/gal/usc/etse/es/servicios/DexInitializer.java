package gal.usc.etse.es.servicios;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gal.usc.etse.es.servicios.repository.MoveRepository;
import gal.usc.etse.es.servicios.repository.PokemonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DexInitializer implements CommandLineRunner {

    private final PokemonRepository pokemonRepository;
    private final MoveRepository moveRepository;
    private final ObjectMapper objectMapper;

    public DexInitializer(PokemonRepository pokemonRepository,
                           MoveRepository moveRepository,
                           ObjectMapper objectMapper) {
        this.pokemonRepository = pokemonRepository;
        this.moveRepository = moveRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ignora campos del JSON que no existan en tus entidades Java
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 1. Cargar Movimientos
        try (InputStream movesStream = new ClassPathResource("data/movimientos.json").getInputStream()) {
            DataWrappers.MovesWrapper movesData = objectMapper.readValue(movesStream, DataWrappers.MovesWrapper.class);
            if (movesData != null && movesData.moves() != null) {
                moveRepository.saveAll(movesData.moves());
                System.out.println("Cargados " + movesData.moves().size() + " movimientos.");
            }
        }

        // 2. Cargar Pokémon
        try (InputStream pokemonStream = new ClassPathResource("data/pokemon.json").getInputStream()) {
            DataWrappers.PokemonWrapper pokemonData = objectMapper.readValue(pokemonStream, DataWrappers.PokemonWrapper.class);
            if (pokemonData != null && pokemonData.pokemon() != null) {
                pokemonRepository.saveAll(pokemonData.pokemon());
                System.out.println("Cargados " + pokemonData.pokemon().size() + " Pokémon.");
            }
        }
    }
}