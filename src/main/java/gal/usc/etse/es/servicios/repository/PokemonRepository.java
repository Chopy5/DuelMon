package gal.usc.etse.es.servicios.repository;

/*
*   Clase dedicada ao acceso de datos dos pokémon
*   Usaremos unha base de datos para os pokémon (estática, prácticamente solo lectura, pero favorece á velocidade de lecturas)
*
*/

// Usamos un interface que no extienede JpaRepository, porque

import gal.usc.etse.es.servicios.model.PokemonType;
import gal.usc.etse.es.servicios.model.entity.Pokemon;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository {

    //Optional porque pueden dar null
    Optional<Pokemon> findById(int id);
    Optional<Pokemon> findByNameIgnoreCase(String name);
    List<Pokemon> searchFiltersIgnoreCase(String prefix, PokemonType type);
    List<Pokemon> findAll();

    void save(Pokemon pokemon);
    void saveAll(List<Pokemon> pokemons);
}
