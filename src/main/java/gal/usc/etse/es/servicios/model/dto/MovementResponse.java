package gal.usc.etse.es.servicios.model.dto;

import gal.usc.etse.es.servicios.model.entity.MoveEffect;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.enums.MoveClass;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.model.enums.Target;
import gal.usc.etse.es.servicios.utils.FormatUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

// SIN validation que no es un DTO de entrada ni entity
@NullMarked
public record MovementResponse(
        String id,
        String name,
        PokemonType type,
        MoveClass category,
        @Nullable Integer power,
        @Nullable Integer accuracy,
        int pp,
        int priority,
        Target target,
        List<MoveEffect> effects
) {
    // Método factoría para mapear directamente desde la entidad JPA sin necesidad de una clase Mapper externa
    public static MovementResponse from(MovementData entity) {
        return new MovementResponse(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getCategory(),
                entity.getPower(),
                entity.getAccuracy(),
                entity.getPp(),
                entity.getPriority(),
                entity.getTarget(),
                entity.getEffects()
        );
    }
}