package gal.usc.etse.es.servicios.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Converter
@Component
public class MoveEffectListConverter implements AttributeConverter<List<MoveEffect>, String> {

    // Jackson necesita que le indiquemos el tipo
    private static final TypeReference<List<MoveEffect>> TYPE_REFERENCE = new TypeReference<>() {};

    private final ObjectMapper mapper;

    public MoveEffectListConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(List<MoveEffect> effects) {
        if (effects == null || effects.isEmpty()) {
            return "[]";
        }
        // Escribimos los datos en la columna con ObjectMapper -> En formato JSON
        // TYPE para que sepa de que tipo era la lista
        return mapper.writerFor(TYPE_REFERENCE).writeValueAsString(effects);
    }

    @Override
    public List<MoveEffect> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        // Leemos los datos con ObjectMapper
        return mapper.readValue(json, TYPE_REFERENCE);
    }
}