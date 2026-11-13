package gal.usc.etse.es.servicios.model.entity;

import gal.usc.etse.es.servicios.model.dto.PokemonResponse;

import java.util.List;

public class PokemonInTeam {

    private PokemonResponse pokemonData;
    private PokemonStats finalStats;
    private List<PokemonMovement> pokemonMovements;

    public PokemonInTeam(PokemonResponse pokemonData, List<PokemonMovement> pokemonMovements) {

    }

    public void validatePokemon(){

    }

    public void calculateFinalStats(){

    }

}
