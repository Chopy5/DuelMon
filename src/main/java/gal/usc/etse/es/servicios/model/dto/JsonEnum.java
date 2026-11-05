package gal.usc.etse.es.servicios.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public interface JsonEnum {
    // Metodo para serializar enums con su nombre en lowercase
    @JsonValue
    default String toJson() {
        return ((Enum<?>) this).name().toLowerCase(Locale.ROOT);
    }
}
