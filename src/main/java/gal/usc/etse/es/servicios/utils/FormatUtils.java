package gal.usc.etse.es.servicios.utils;

import gal.usc.etse.es.servicios.exceptions.InvalidEnumValueException;
import org.jspecify.annotations.Nullable;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FormatUtils {

    private static final Pattern ACCENTS = Pattern.compile("\\p{M}+");      // acentos
    private static final Pattern PUNCTUATION = Pattern.compile("['’.:]");          // puntuacion
    private static final Pattern SEPARATORS = Pattern.compile("[\\s_\\-]+");   // espacios, _ y - (uno o varios)
    private static final Pattern EDGE_DASHES = Pattern.compile("^-+|-+$");  // - que queden en los extemos

    //Incluimos un constructor privado para evitar que se cree uno público y la haga instanciable
    private FormatUtils() {
        throw new UnsupportedOperationException("Es una clase de utilidad, no se puede instanciar");
    }

    public static String toSlug(@Nullable String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String s = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);  // Separa los acentos de las letras
        s = ACCENTS.matcher(s).replaceAll(""); // quita acentos
        s = PUNCTUATION.matcher(s).replaceAll("");  //quita puntuacion
        s = SEPARATORS.matcher(s).replaceAll("-");  // quita o reduce separadores
        s = EDGE_DASHES.matcher(s).replaceAll("");  // quita guiones en los extremos

        return s.toLowerCase(Locale.ROOT);  // minusculas
    }

    public static <E extends Enum<E>> E toEnum(Class<E> type, String text) {
        String name = toSlug(text).replace('-', '_').toUpperCase(Locale.ROOT);  // Pasa primero a slug y luego cambia los - por _ y pasa MAYUS
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException ignored) {
            String allowed = Arrays.stream(type.getEnumConstants())
                    .map(c -> toSlug(c.name()))
                    .collect(Collectors.joining(", "));
            throw new InvalidEnumValueException(
                    "Valor '" + text + "' no válido para " + type.getSimpleName()
                            + ". Valores aceptados: [" + allowed + "]");
        }
    }




}
