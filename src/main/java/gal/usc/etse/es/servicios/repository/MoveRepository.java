package gal.usc.etse.es.servicios.repository;

import gal.usc.etse.es.servicios.model.MoveClass;
import gal.usc.etse.es.servicios.model.entity.Move;
import gal.usc.etse.es.servicios.model.PokemonType;

import java.util.List;
import java.util.Optional;

public interface MoveRepository {
    //Optional porque pueden dar null
    Optional<Move> findById(String id);
    List<Move> searchFiltersIgnoreCase(String prefix, PokemonType type, MoveClass moveClass);
    List<Move> findAll();

    void save(Move move);
    void saveAll(List<Move> moves);
}
