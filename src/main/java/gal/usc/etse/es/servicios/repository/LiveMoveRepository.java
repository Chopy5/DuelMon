package gal.usc.etse.es.servicios.repository;

import gal.usc.etse.es.servicios.model.MoveClass;
import gal.usc.etse.es.servicios.model.entity.Move;
import gal.usc.etse.es.servicios.model.PokemonType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LiveMoveRepository implements MoveRepository {
    private final Map<String, Move> mapMoves = new ConcurrentHashMap<>();

    @Override
    public Optional<Move> findById(String id) {
        return Optional.ofNullable(mapMoves.get(id));
    }

    @Override
    public List<Move> searchFiltersIgnoreCase(String prefix, PokemonType type, MoveClass moveClass) {
        if ((prefix == null || prefix.isBlank()) && type == null && moveClass == null) {
            return findAll();
        }

        boolean noPrefix = prefix == null || prefix.isBlank();
        boolean noType = type == null;
        boolean noMoveClass = moveClass == null;

        // Tenemos que crear nueva variable para poder hacer el .toLowerCase antes
        // Las entradas de las lambdas deben ser constantes, finales
        String searchPrefix = noPrefix ? null : prefix.trim().toLowerCase();

        return mapMoves.values().stream()
                .filter(m -> noPrefix || (m.name() != null && m.name().toLowerCase().startsWith(searchPrefix)))
                .filter(m -> noType || (m.type() != null && m.type().equals(type)))
                .filter(m -> noMoveClass || (m.moveClass() != null && m.moveClass().equals(moveClass)))
                .toList();
    }

    @Override
    public List<Move> findAll() {
        //Devolvemos .lista inmutable, para concurrencia -> foto fija, si se modifica después no cambia para esta vista
        return List.copyOf(mapMoves.values());
    }

    @Override
    public void save(Move move) {
        if (move != null && move.id() != null) {
            mapMoves.put(move.id(), move);
        }
    }

    @Override
    public void saveAll(List<Move> moves) {
        if (moves != null) {
            moves.forEach(this::save);
        }
    }

}
