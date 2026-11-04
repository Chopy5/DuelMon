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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    // Obtener pokemonData por id
    public @NonNull PokemonData getPokemonById(int id) {
        if (id <= 0) {
            throw new InvalidInputException("El ID del PokemonData debe ser un número entero positivo: " + id);
        }

        // Devuelve pokemonData, o si no lo encuentra avisa
        return pokemonRepository.findById(id).orElseThrow(() -> new PokemonNotFoundException("PokemonData no encontrado con ID: " + id));
    }

   // Obtener pokemonData por nombre
    public @NonNull PokemonData getPokemonByName(@NonNull String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("El nombre del PokemonData no puede estar vacío");
        }

        String cleanName = FormatUtils.sanitizeSearchEntry(name);

        return pokemonRepository.findByNameIgnoreCase(cleanName).orElseThrow(() -> new PokemonNotFoundException("PokemonData no encontrado con nombre: " + cleanName));
    }

    // Permitimos que pueda ser el nombre o el id
    // Despachamos a los metodos anteriores
    public @NonNull PokemonData getPokemon(@NonNull String entry) {
        if (entry == null || entry.isBlank()) {
            throw new InvalidInputException("La entrada del PokemonData no puede estar vacía");
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

    // Para búsquedas de usuario de pokemonData
    public @NonNull List<PokemonData> searchPokemon(String prefix, String typeStr) {
        String cleanPrefix = FormatUtils.sanitizeSearchEntry(prefix);

        PokemonType type = null;

        if (typeStr != null && !typeStr.isBlank()) {
            // Cogemos el Optional de tipo
            Optional<PokemonType> typeOpt = PokemonType.convertStringToType(typeStr);

            // Si esta vacio devolvemos lista vacia inmutable
            if (typeOpt.isEmpty()) {
                return List.of();
            }

            // Si no esta vacio asignamos el tipo
            type = typeOpt.get();
        }

        return pokemonRepository.searchFiltersIgnoreCase(cleanPrefix, type); // NO usa SQL
    }

    // MOVIMIENTOS

    // Movimiento por id -> se pasa nombre y se convierte a formato kebab para el id
    public @NonNull MovementData getMove(@NonNull String idMove) {
        if (idMove == null || idMove.isBlank()) {
            throw new InvalidInputException("El ID del movimiento no puede estar vacío");
        }

        String kebabId = FormatUtils.toKebabCase(idMove);

        return moveRepository.findById(kebabId)
                .orElseThrow(() -> new MoveNotFoundException("Movimiento no encontrado con ID: " + kebabId));
    }

    // Para consultar movimientos de un pokemonData
    public @NonNull List<MovementData> getPokemonMoves(@NonNull String idPokemon) {
        // Reutiliza getPokemon (si no existe, lanza 404 automáticamente)
        PokemonData pokemonData = getPokemon(idPokemon);
        Set<String> moveIds = pokemonData.moveIds();
        if (moveIds == null) {
            return List.of();
        } else {
            return moveIds.stream().map(this::getMove).toList();
        }
    }

    // Para búsquedas de usuario de pokemonData
    public @NonNull List<MovementData> searchMoves(String prefix, String typeStr, String moveClassStr) {
        String cleanPrefix = FormatUtils.sanitizeSearchEntry(prefix);

        PokemonType type = null;
        MoveClass moveClass = null;

        if (typeStr != null && !typeStr.isBlank()) {
            // Cogemos el Optional de tipo
            Optional<PokemonType> typeOpt = PokemonType.convertStringToType(typeStr);

            // Si esta vacio devolvemos lista vacia
            if (typeOpt.isEmpty()) {
                return List.of();
            }

            // Si no esta vacio asignamos el tipo
            type = typeOpt.get();
        }

        if (moveClassStr != null && !moveClassStr.isBlank()) {
            // Cogemos el Optional de clase
            Optional<MoveClass> moveClassOpt = MoveClass.convertStringToMoveClass(moveClassStr);

            // Si esta vacio devolvemos lista vacia
            if (moveClassOpt.isEmpty()) {
                return List.of();
            }

            // Si no esta vacio asignamos la clase
            moveClass = moveClassOpt.get();
        }

        return moveRepository.searchFiltersIgnoreCase(cleanPrefix, type, moveClass); // NO usa SQL
    }
}