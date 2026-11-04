package gal.usc.etse.es.servicios.model.dto;

import java.util.Arrays;
import java.util.Optional;

public enum PokemonType {

    NORMAL,
    FIRE,
    WATER,
    GRASS,
    ELECTRIC,
    ICE,
    FIGHTING,
    POISON,
    GROUND,
    FLYING,
    PSYCHIC,
    BUG,
    ROCK,
    GHOST,
    DRAGON;

    // Metodo para pasar a tipo de pokemonData
    public static Optional<PokemonType> convertStringToType(String entry) {

        // Si está vacio no encaja con ninguno y devuelve Optional vacio
        if (entry == null || entry.isBlank()) {
            return Optional.empty();
        }

        // Si hay lo limpia
        String clean = entry.trim();

        // Buscamos si hay algun tipo cuyo nombre coincida y devolvemos el optional
        return Arrays.stream(values())  // values() son los valores del enum
                .filter(p -> p.name().equalsIgnoreCase(clean))  // name en vez de toString() para evitar que lo sobreescriban
                .findFirst();
    }
}
