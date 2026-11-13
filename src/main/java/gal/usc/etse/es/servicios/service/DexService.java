package gal.usc.etse.es.servicios.service;

import gal.usc.etse.es.servicios.exceptions.InvalidInputException;
import gal.usc.etse.es.servicios.exceptions.MoveNotFoundException;
import gal.usc.etse.es.servicios.exceptions.PokemonNotFoundException;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.model.dto.MovementResponse;
import gal.usc.etse.es.servicios.model.dto.PokemonResponse;
import gal.usc.etse.es.servicios.model.enums.MoveClass;
import gal.usc.etse.es.servicios.repository.MovementDataRepository;
import gal.usc.etse.es.servicios.repository.PokemonDataRepository;
import gal.usc.etse.es.servicios.utils.FormatUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

// Clase de servicio de pokedex
@Service
@Transactional(readOnly = true)
public class DexService {

    private static final Pattern DIGITS = Pattern.compile("[0-9]+");

    private final PokemonDataRepository pokemonRepository;
    private final MovementDataRepository moveRepository;

    private final String spriteUrl;

    @Autowired
    public DexService(PokemonDataRepository pokemonRepository, MovementDataRepository moveRepository, @Value("${duelmon.sprites.base-url}") String spriteUrl) {
        this.pokemonRepository = pokemonRepository;
        this.moveRepository = moveRepository;
        this.spriteUrl = spriteUrl;
    }

    // POKEMON

    // Método para obtener un pokemon concreto
    private PokemonData dispatchPokemon(String pokemonIdentifier) {
        if (pokemonIdentifier == null || pokemonIdentifier.isBlank()) {
            throw new InvalidInputException("La entrada del Pokémon no puede estar vacía");
        }

        String clean = pokemonIdentifier.trim();
        if (DIGITS.matcher(clean).matches()) {
            int number;
            try {
                number = Integer.parseInt(clean);
            } catch (NumberFormatException e) {
                throw new InvalidInputException("El número del Pokémon no es válido: " + clean);
            }
            return pokemonRepository.findWithLearnsetByNumber(number)
                    .orElseThrow(() -> new PokemonNotFoundException("Pokémon no encontrado con número: " + number));
        }

        String id = FormatUtils.toSlug(clean);
        if (id.isEmpty()) {
            throw new InvalidInputException("El identificador del Pokémon no es válido: '" + pokemonIdentifier + "'");
        }
        return pokemonRepository.findWithLearnsetById(id)
                .orElseThrow(() -> new PokemonNotFoundException("Pokémon no encontrado con ID: " + id));
    }

    // Método para obtener un pokemon concreto
    public PokemonResponse getPokemon(String pokemonIdentifier) {
        return PokemonResponse.from(dispatchPokemon(pokemonIdentifier),spriteUrl);
    }

    // Obtener pokemon por id slug
    public PokemonResponse getPokemonById(String pokemonId) {
        String id = FormatUtils.toSlug(pokemonId);

        if (id.isEmpty()) {
            throw new InvalidInputException("El identificador del Pokémon no es válido: '" + pokemonId + "'");
        }

        PokemonData entity = pokemonRepository.findWithLearnsetById(id)
                .orElseThrow(() -> new PokemonNotFoundException("Pokémon no encontrado con ID: " + id));

        return PokemonResponse.from(entity,spriteUrl);
    }

    // Para búsquedas de pokemon
    public List<PokemonResponse> searchPokemon(@Nullable String prefix, @Nullable PokemonType type) {
        String idPrefix = FormatUtils.toSlug(prefix);
        return pokemonRepository.getPokemonByFilters(idPrefix, type).stream()
                .map(pokemon -> PokemonResponse.from(pokemon, spriteUrl))
                .toList();
    }

    // MOVIMIENTOS

    // Movimiento por id -> se pasa nombre y se convierte a formato slug para el id
    public MovementResponse getMove(String moveId) {
        String id = FormatUtils.toSlug(moveId);

        if (id.isEmpty()) {
            throw new InvalidInputException("El ID del movimiento no puede estar vacío o no es válido");
        }

        MovementData entity = moveRepository.findById(id)
                .orElseThrow(() -> new MoveNotFoundException("Movimiento no encontrado con ID: " + id));

        return MovementResponse.from(entity);
    }

    public List<MovementResponse> searchPokemonMoves(String pokemonIdentifier, @Nullable String prefix, @Nullable PokemonType type, @Nullable MoveClass category) {
        PokemonData pokemon = dispatchPokemon(pokemonIdentifier);
        String idPrefix = FormatUtils.toSlug(prefix);

        return pokemon.getLearnset().stream()
                .filter(m -> idPrefix.isEmpty() || m.getId().startsWith(idPrefix))
                .filter(m -> type == null || m.getType() == type)
                .filter(m -> category == null || m.getCategory() == category)
                .sorted(Comparator.comparing(MovementData::getId))
                .map(MovementResponse::from)
                .toList();
    }

    // Para búsquedas de movimientos de un pokemon
    public List<MovementResponse> searchMoves(@Nullable String prefix, @Nullable PokemonType type, @Nullable MoveClass category) {
        String idPrefix = FormatUtils.toSlug(prefix);

        return moveRepository.getMovesByFilters(idPrefix, type, category).stream()
                .map(MovementResponse::from)
                .toList();
    }
}