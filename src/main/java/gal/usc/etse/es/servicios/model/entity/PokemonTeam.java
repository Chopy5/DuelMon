package gal.usc.etse.es.servicios.model.entity;

import java.util.ArrayList;
import java.util.List;

public class PokemonTeam {

    private List<PokemonInTeam> pokemonList;

    public PokemonTeam(List<PokemonInTeam> pokemonList) { // throws OqueseaException

        isTeamValid(pokemonList);

        this.pokemonList = pokemonList;

    }

    // Podería definirse nalgún outro sitio, se desexamos validar noutro lugar centralizado (buscar cheaters?)

    public void isTeamValid(List<PokemonInTeam> pokemonList){ // throws OqueseaException

        if(pokemonList == null){
            // throw new OqueseaException ("Pokémon list cannot be null");
        }

        if(pokemonList.size()>6){
            // throw new OqueseaException ("You can´t have more than 6 pokémon in your team");
        }

        List<PokemonData> includedSpecies = new ArrayList<>();

        for(PokemonInTeam pokemon : pokemonList){

            // Obtener los datos del pokemon

            // Comprobar que la especie no esté repetida

            // throw new OqueseaException (nombrePokemon + " has already been included. (You can´t have two pokemon of the same species.)?"

            // Comprobar que el pokémon tenga 4 movimientos

            // throw new OqueseaException (nombrePokemon + " doesn`t have four moves. (Every pokemon must have four moves.)?"

            // Para cada movimiento, comprobar el pokémon que es capaz de aprenderlos

            // throw new OqueseaException (nombrePokemon + " can´t learn " + nombreMovemento + ".");

        }

    }

}
