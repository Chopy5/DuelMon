package gal.usc.etse.es.servicios.repository;

import gal.usc.etse.es.servicios.model.MoveClass;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.PokemonType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LiveMoveRepository implements MoveRepository {
    private final Map<String, MovementData> mapMoves = new ConcurrentHashMap<>();

    @Override
    public Optional<MovementData> findById(String id) {
        return Optional.ofNullable(mapMoves.get(id));
    }

    @Override
    public List<MovementData> searchFiltersIgnoreCase(String prefix, PokemonType type, MoveClass moveClass) {
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
    public List<MovementData> findAll() {
        //Devolvemos .lista inmutable, para concurrencia -> foto fija, si se modifica después no cambia para esta vista
        return List.copyOf(mapMoves.values());
    }

    @Override
    public void save(MovementData movementData) {
        if (movementData != null && movementData.id() != null) {
            mapMoves.put(movementData.id(), movementData);
        }
    }

    @Override
    public void saveAll(List<MovementData> movementData) {
        if (movementData != null) {
            movementData.forEach(this::save);
        }
    }

}
