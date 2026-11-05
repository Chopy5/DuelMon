package gal.usc.etse.es.servicios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;

import java.util.List;

public class DataWrappers {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovesWrapper(@JsonProperty("moves") List<MovementData> movementData) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonWrapper(@JsonProperty("pokemon") List<PokemonData> pokemonData) {}
}