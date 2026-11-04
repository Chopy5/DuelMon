package gal.usc.etse.es.servicios.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.es.servicios.model.MoveClass;
import gal.usc.etse.es.servicios.model.PokemonType;

public record Move(
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
