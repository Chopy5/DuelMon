package gal.usc.etse.es.servicios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;

import java.util.List;

public class DataWrappers {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovesWrapper(List<MovementData> movementData) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonWrapper(List<PokemonData> pokemonData) {}
}