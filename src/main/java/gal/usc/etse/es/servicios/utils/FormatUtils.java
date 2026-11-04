package gal.usc.etse.es.servicios.utils;

import java.util.Locale;

public final class FormatUtils {

    //Incluimos un constructor privado para evitar que se cree uno público y la haga instanciable
    private FormatUtils() {
        throw new UnsupportedOperationException("Es una clase de utilidad, no se puede instanciar");
    }

    // Pasa entradas de texto a kebab-case, en minúsculas
    public static String toKebabCase(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        return input.trim()
                // Minusculas
                .toLowerCase(Locale.ROOT)
                // Elimina apóstrofes y puntos
                .replaceAll("['.]", "")
                // Sustituye espacios o _ por un guion
                .replaceAll("[\\s_]+", "-");
    }

    // Normaliza entradas de busqueda por nombre
    public static String sanitizeSearchEntry(String input) {
        if(input == null || input.isBlank()) {
            return "";
        }

        return input.trim()
                // Convierte guiones en espacios
                .replace("-", " ")
                // Reduce varios espacios seguidos
                .replaceAll("\\s+", " ");
    }


}
