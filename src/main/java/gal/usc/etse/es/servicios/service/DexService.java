package gal.usc.etse.es.servicios.service;

import gal.usc.etse.es.servicios.exceptions.InvalidInputException;
import gal.usc.etse.es.servicios.exceptions.MoveNotFoundException;
import gal.usc.etse.es.servicios.exceptions.PokemonNotFoundException;
import gal.usc.etse.es.servicios.model.dto.MoveClass;
import gal.usc.etse.es.servicios.model.dto.PokemonType;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;
import gal.usc.etse.es.servicios.repository.MoveRepository;
import gal.usc.etse.es.servicios.repository.PokemonRepository;
import gal.usc.etse.es.servicios.utils.FormatUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

// Clase de servicio de pokedex
@Service
public class DexService {

    private final PokemonRepository pokemonRepository;
    private final MoveRepository moveRepository;

    @Autowired
    public DexService(PokemonRepository pokemonRepository, MoveRepository moveRepository) {
        this.pokemonRepository = pokemonRepository;
        this.moveRepository = moveRepository;
    }

    // POKEMON


    // Permitimos que pueda ser el nombre o el id
    // Despachamos a los metodos anteriores
    public @NonNull PokemonData getPokemon(@NonNull String entry) {
        if (entry.isBlank()) {
            throw new InvalidInputException("La entrada del Pokemon no puede estar vacía");
        }

        String clean = entry.trim();


        // Si son solo dígitos busca por numero (id)
        if (clean.matches("[0-9]+")) {
            try {
                int id = Integer.parseInt(clean);
                return getPokemonById(id);
            } catch (NumberFormatException e) {
                throw new InvalidInputException("El ID numérico es erróneo: " + clean);
            }
        }

        // Si es texto busca por nombre
        return getPokemonByName(clean);
    }

    // Obtener pokemon por id
    private @NonNull PokemonData getPokemonById(int id) {
        if (id <= 0) {
            throw new InvalidInputException("El ID del Pokemon debe ser un número entero positivo: " + id);
        }

        // Devuelve pokemon, o si no lo encuentra avisa
        return pokemonRepository.findById(id)
            .orElseThrow(() -> new PokemonNotFoundException("Pokemon no encontrado con ID: " + id));
    }

   // Obtener pokemon por nombre
    private @NonNull PokemonData getPokemonByName(@NonNull String name) {

        //Ya no hace falta comprobar blank, asumimos que llega bien del dispatcher

        String cleanName = FormatUtils.sanitizeSearchEntry(name);

        return pokemonRepository.findByNameIgnoreCase(cleanName)
                .orElseThrow(() -> new PokemonNotFoundException("Pokemon no encontrado con nombre: " + cleanName));
    }

    // Para búsquedas de usuario de pokemon
    // Prefix no es
    public @NonNull List<PokemonData> searchPokemon(@Nullable String prefix,@Nullable PokemonType type) {
        String cleanPrefix = FormatUtils.sanitizeSearchEntry(prefix);

        if (cleanPrefix.isEmpty() && type == null) {
            return pokemonRepository.findAll();
        }

        return pokemonRepository.searchFiltersIgnoreCase(cleanPrefix, type); // NO usa SQL
    }

    // MOVIMIENTOS

    // Movimiento por id -> se pasa nombre y se convierte a formato kebab para el id
    public @NonNull MovementData getMove(@NonNull String idMove) {
        if (idMove.isBlank()) {
            throw new InvalidInputException("El ID del movimiento no puede estar vacío");
        }

        String kebabId = FormatUtils.toKebabCase(idMove);

        return moveRepository.findById(kebabId)
                .orElseThrow(() -> new MoveNotFoundException("Movimiento no encontrado con ID: " + kebabId));
    }

    // Para consultar movimientos de un pokemon
    public @NonNull List<MovementData> getPokemonMoves(@NonNull String idPokemon, @Nullable String prefix, @Nullable PokemonType type, @Nullable MoveClass moveClass) {
        // Reutiliza getPokemon (si no existe, lanza 404 automáticamente)
        PokemonData pokemon = getPokemon(idPokemon);
        Set<String> moveIds = pokemon.moveIds();

        if (moveIds == null || moveIds.isEmpty()) {
            return List.of();
        }

        String cleanPrefix = FormatUtils.sanitizeSearchEntry(prefix).toLowerCase(Locale.ROOT);

        // No es importante hacer atajos de filtros para findAll, es solo una cantidad pequeña
        if (cleanPrefix.isEmpty() && type == null && moveClass == null) {
            return moveIds.stream()
                    .map(this::getMove)
                    .toList();
        }

        return moveIds.stream()
                .map(this::getMove)
                .filter(move -> cleanPrefix.isEmpty() || move.name().toLowerCase(Locale.ROOT).startsWith(cleanPrefix))
                .filter(move -> type == null || move.type() == type)
                .filter(move -> moveClass == null || move.moveClass() == moveClass)
                .toList();
    }

    // Para búsquedas de movimidntos de un pokemon
    public @NonNull List<MovementData> searchMoves(@Nullable String prefix, @Nullable PokemonType type, @Nullable MoveClass moveClass) {
        String cleanPrefix = FormatUtils.sanitizeSearchEntry(prefix);
        return moveRepository.searchFiltersIgnoreCase(cleanPrefix, type, moveClass); // NO usa SQL
    }
}