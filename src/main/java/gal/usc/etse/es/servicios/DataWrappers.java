package gal.usc.etse.es.servicios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gal.usc.etse.es.servicios.model.entity.Move;
import gal.usc.etse.es.servicios.model.entity.Pokemon;

import java.util.List;

public class DataWrappers {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovesWrapper(List<Move> moves) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonWrapper(List<Pokemon> pokemon) {}
}