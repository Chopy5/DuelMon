package gal.usc.etse.es.servicios.model.dto;

import java.util.Arrays;
import java.util.Optional;

public enum MoveClass {
    PHYSICAL,
    SPECIAL,
    STATUS;

    // Metodo para pasar a clase de movimiento
    public static Optional<MoveClass> convertStringToMoveClass(String entry) {

        // Si está vacio no encaja con ninguno y devuelve Optional vacio
        if (entry == null || entry.isBlank()) {
            return Optional.empty();
        }

        // Si hay lo limpia
        String clean = entry.trim();

        // Buscamos si hay alguna clase cuyo nombre coincida y devolvemos el optional
        return Arrays.stream(values())  // values() son los valores del enum
                .filter(p -> p.name().equalsIgnoreCase(clean))  // name en vez de toString() para evitar que lo sobreescriban
                .findFirst(); // El .findFirst() de un stream devuelve un Optional
    }
}
