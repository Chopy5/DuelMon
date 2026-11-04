package gal.usc.etse.es.servicios.repository;

import gal.usc.etse.es.servicios.model.dto.MoveClass;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.dto.PokemonType;

import java.util.List;
import java.util.Optional;

public interface MoveRepository {
    //Optional porque pueden dar null
    Optional<MovementData> findById(String id);
    List<MovementData> searchFiltersIgnoreCase(String prefix, PokemonType type, MoveClass moveClass);
    List<MovementData> findAll();

    void save(MovementData movementData);
    void saveAll(List<MovementData> movementData);
}
