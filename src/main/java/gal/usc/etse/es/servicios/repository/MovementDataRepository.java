package gal.usc.etse.es.servicios.repository;


import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.enums.MoveClass;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MovementDataRepository extends JpaRepository<MovementData, String> {

    // Aqui no necesitamos entity graph porque no tiene atributos que provengan de relaciones

    @Query("""
        SELECT m FROM MovementData m
        WHERE (LOWER(m.id) LIKE LOWER(CONCAT(:idPrefix, '%')))
        AND (:type IS NULL OR m.type = :type)
      AND (:category IS NULL OR m.category = :category)
    """)
    List<MovementData> getMovesByFilters(String idPrefix, @Nullable PokemonType type, @Nullable MoveClass category);



}
