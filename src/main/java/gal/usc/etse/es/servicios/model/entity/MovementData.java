package gal.usc.etse.es.servicios.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.es.servicios.model.dto.MoveClass;
import gal.usc.etse.es.servicios.model.dto.PokemonType;

public record MovementData(
        String id,
        String name,
        PokemonType type,
        @JsonProperty("category") MoveClass moveClass,
        Integer power,
        Integer accuracy,
        int pp,
        int priority,
        String target
        //List<MoveEffect> effects
) {}
