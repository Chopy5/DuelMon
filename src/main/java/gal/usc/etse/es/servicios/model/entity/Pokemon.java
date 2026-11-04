package gal.usc.etse.es.servicios.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.es.servicios.model.PokemonType;
import gal.usc.etse.es.servicios.model.Stats;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Pokemon(
        int id,
        String name,
        List<PokemonType> types,    // Orden de tipos importa para STAB
        Stats stats,
        @JsonProperty("moves") Set<String> moveIds
) {}
