package gal.usc.etse.es.servicios.repository;

/*
*   Clase dedicada ao acceso de datos dos pokémon
*   Usaremos unha base de datos para os pokémon (estática, prácticamente solo lectura, pero favorece á velocidade de lecturas)
*
*/

// Usamos un interface que no extienede JpaRepository, porque

import gal.usc.etse.es.servicios.model.entity.PokemonData;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.model.dto.PokemonResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonDataRepository extends JpaRepository<PokemonData, String> {

    // Versiones lazy, que traen solo los datos de la propia entidad, pero no su learnset hasta que se pida
    // FindById y findAll vienen ya por defecto


    // Versiones con EntityGraph, que traen el learnset
    @EntityGraph(attributePaths = "learnset")
    Optional<PokemonData> findWithLearnsetById(String id);

    @EntityGraph(attributePaths = "learnset")
    Optional<PokemonData> findWithLearnsetByNumber(int number);

    // search solo tiene version lazy
    @Query("""
        SELECT p FROM PokemonData p
        WHERE (LOWER(p.id) LIKE LOWER(CONCAT(:idPrefix, '%')))
        AND (:type IS NULL OR p.primaryType = :type OR p.secondaryType = :type)
    """)
    List<PokemonData> getPokemonByFilters(String idPrefix, @Nullable PokemonType type);
}
