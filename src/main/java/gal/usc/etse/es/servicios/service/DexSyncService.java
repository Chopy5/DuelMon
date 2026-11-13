package gal.usc.etse.es.servicios.service;

import gal.usc.etse.es.servicios.model.dto.MovementResponse;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;
import gal.usc.etse.es.servicios.model.entity.PokemonStats;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.repository.MovementDataRepository;
import gal.usc.etse.es.servicios.repository.PokemonDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

// Componen para que se gestione por Spring al iniciar
@Service
public class DexSyncService {   // ApplicationRunner es una interfaz que se ejecuta una única vez, en el arranque del servidor

    // Records internos que reflejan exactamente el esquema de los JSON de entrada
    record PokemonEntry(
            String id,
            int number,
            String name,
            List<PokemonType> types,
            PokemonStats stats,
            List<String> moves
    ) {}

    // Records para almacenar cada archivo y sus datos
    record PokemonFile(String version, int totalPokemon, List<PokemonEntry> pokemon) {}
    record MovesFile(String version, int totalMoves, List<MovementResponse> moves) {}

    private final ObjectMapper mapper;
    private final Resource pokemonResource;
    private final Resource movesResource;
    private final MovementDataRepository moveRepository;
    private final PokemonDataRepository pokemonRepository;

    // Constructor que toma de application.properties las localizaciones de los archivos
    // Ademas se le inyectan los repository
    // No hace falta poner @Autowired si es el unico constructor
    public DexSyncService(
            ObjectMapper mapper,
            @Value("${duelmon.data.pokemon}") Resource pokemonResource,
            @Value("${duelmon.data.moves}") Resource movesResource,
            MovementDataRepository moveRepository,
            PokemonDataRepository pokemonRepository) {
        this.mapper = mapper;
        this.pokemonResource = pokemonResource;
        this.movesResource = movesResource;
        this.moveRepository = moveRepository;
        this.pokemonRepository = pokemonRepository;
    }

    @Transactional
    public void syncDex() {    // Metodo que se ejecuta al inicio de la aplicacion, una vez Spring ha enlazado todo
        PokemonFile pokemonFile;
        MovesFile movesFile;

        // Obtenemos los streams dentro del try para que se cierren automáticamente al terminar
        try (InputStream pokemonStream = pokemonResource.getInputStream();
             InputStream movementStream = movesResource.getInputStream()) {

            // Leemos los archivos -> Jackson los mapea a los records definidos
            pokemonFile = mapper.readValue(pokemonStream, PokemonFile.class);
            movesFile = mapper.readValue(movementStream, MovesFile.class);
        } catch (IOException | JacksonException e) {
            throw new IllegalStateException("No se pudieron leer los ficheros de la Pokédex", e);
        }

        // Validamos las referencias entre movimientos y pokemon
        validateDexFiles(pokemonFile, movesFile);

        // Por cada movimiento del archivo -> nos llega el DTO, no entidad
        // Creamos una entidad para guardar en el repository
        List<MovementData> movesToSave = new ArrayList<>();
        for (MovementResponse dto : movesFile.moves()) {
            movesToSave.add(new MovementData(
                    dto.id(), dto.name(), dto.type(), dto.category(),
                    dto.power(), dto.accuracy(), dto.pp(), dto.priority(),
                    dto.target(), dto.effects()
            ));
        }

        // Usamos saveAll (por defecto) para guardar los movimientos en el repositorio -> crea los nuevos y actualiza los que ya existen
        List<MovementData> savedMoves = moveRepository.saveAll(movesToSave);

        // Guardamos los movimientos que acabamos de guardar en un mapa para luego pasarselos a los pokemon
        Map<String, MovementData> movesMap = new HashMap<>();
        for (MovementData move : savedMoves) {
            movesMap.put(move.getId(), move);
        }

        // Repetimos el mismo proceso para los pokemon
        // Por cada DTO de pokemon leido del archivo -> se añade a una lista de pokemon a guardar
        List<PokemonData> pokemonToSave = new ArrayList<>();
        for (PokemonEntry entry : pokemonFile.pokemon()) {

            // Metemos los movimientos en un LinkedHashSet para que mantengan el mismo orden (no importa por ahora, pero podria en el futuro)
            Set<MovementData> learnset = new LinkedHashSet<>();
            // Por cada movimiento cogemos su entidad del mapa anterior y lo insertamos en el learnset
            if (entry.moves() != null) {
                for (String moveId : entry.moves()) {
                    learnset.add(movesMap.get(moveId));
                }
            }

            // Fijamos los tipos
            PokemonType primaryType = entry.types().get(0);
            // El secundario es opcional
            PokemonType secondaryType = entry.types().size() > 1 ? entry.types().get(1) : null;

            // Añadimos el pokemon a la lista a guardar
            pokemonToSave.add(new PokemonData(
                    entry.id(), entry.number(), entry.name(),
                    primaryType, secondaryType, entry.stats(), learnset
            ));
        }

        // Guardamos los datos de los pokemon
        pokemonRepository.saveAll(pokemonToSave);

        // USAMOS LOG????????
    }

    // Funcion para validar los archivos json a cargar
    // Valida tamaños de los archivos, entradas duplicadas e inconsistencias de referencia
    // Valida además que la lista de tipos tenga 2 tipos ya (esto afecta en la conversion a entidades JPA, pero ya lo miramos aqui)
    // Comprobamos que el id no sea null o vacio para evitar comportamientos inesperados -> PUEDE QUE NO HAGA FALTA
    // No valida valores concretos, tipos o nulos, eso va en las reglas de la entidad JPA y lo detectará Jackson os Postgre
    private void validateDexFiles(PokemonFile pokemonFile, MovesFile movesFile) {

        // Validamos que los ficheros existen y contienen los datos declarados
        if (pokemonFile == null || pokemonFile.pokemon() == null
                || movesFile == null || movesFile.moves() == null) {
            throw new IllegalStateException("Los ficheros de la Pokédex deben contener las listas 'pokemon' y 'moves'");
        }

        if (pokemonFile.totalPokemon() != pokemonFile.pokemon().size()) {
            throw new IllegalStateException("pokemon.json declara " + pokemonFile.totalPokemon() + " Pokemon pero contiene " + pokemonFile.pokemon().size());
        }

        if (movesFile.totalMoves() != movesFile.moves().size()) {
            throw new IllegalStateException("El fichero de movimientos declara " + movesFile.totalMoves() + " movimientos pero contiene " + movesFile.moves().size());
        }

        List<String> validationErrors = new ArrayList<>();

        // Guardamos los IDs de movimientos ya vistos para detectar duplicados
        Set<String> seenMoveIds = new HashSet<>();

        // Recorremos todos los movimientos del archivo
        for (MovementResponse move : movesFile.moves()) {
            // Comprobamos que tenga id valido
            if (move.id() == null || move.id().isBlank()) {
                validationErrors.add("Hay un movimiento en moves.json sin 'id' definido");
                continue; // Saltamos al siguiente para no meter null en el Set
            }

            // Añadimos los movimientos no repetidos, y si hay uno repetido lo añadimos a la lista de errores
            if (!seenMoveIds.contains(move.id())) {
                seenMoveIds.add(move.id());
            } else {
                validationErrors.add("Movimiento duplicado en moves.json: '" + move.id() + "'");
            }
        }

        // Guardamos los IDs de Pokémon ya vistos para detectar duplicados
        Set<String> seenPokemonIds = new HashSet<>();


        // Recorremos todos los pokemon del archivo
        for (PokemonEntry p : pokemonFile.pokemon()) {

            // Comprobamos que tenga id valido
            if (p.id() == null || p.id().isBlank()) {
                validationErrors.add("Hay un Pokémon en pokemon.json sin 'id' definido");
                continue;
            }

            // Si alguno se repide mostramos error
            if (!seenPokemonIds.contains(p.id())) {
                seenPokemonIds.add(p.id());
            } else {
                validationErrors.add("Pokémon duplicado en pokemon.json: '" + p.id() + "'");
            }

            // Si no tiene 1-2 tipos, error
            if (p.types() == null || p.types().isEmpty() || p.types().size() > 2) {
                validationErrors.add("El Pokémon '" + p.id() + "' debe tener 1 o 2 tipos");
            }

            // Nos aseguramos de que todos los movimientos de su learnet existen en moves.json
            if (p.moves() != null) {
                for (String moveId : p.moves()) {
                    if (!seenMoveIds.contains(moveId)) {
                        validationErrors.add(
                                "El Pokémon '" + p.id() + "' referencia el movimiento inexistente '" + moveId + "'"
                        );
                    }
                }
            }
        }


        // Si se encontró algún error, no notificamos y lanzamos excepción
        if (!validationErrors.isEmpty()) {
            throw new IllegalStateException(
                    "Errores de validación en los ficheros de la Pokédex (" + validationErrors.size() + "): "
                            + String.join("; ", validationErrors));
        }
    }
}