package gal.usc.etse.es.servicios.repository;

import gal.usc.etse.es.servicios.model.dto.MoveClass;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.dto.PokemonType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LiveMoveRepository implements MoveRepository {
    private final Map<String, MovementData> mapMoves = new ConcurrentHashMap<>();

    @Override
    public @NonNull Optional<MovementData> findById(@NonNull String id) {
        return Optional.ofNullable(mapMoves.get(id));
    }

    @Override
    public @NonNull List<MovementData> searchFiltersIgnoreCase(@Nullable String prefix, @Nullable PokemonType type, @Nullable MoveClass moveClass) {
        if ((prefix == null || prefix.isBlank()) && type == null && moveClass == null) {
            return findAll();
        }

        boolean noPrefix = prefix == null || prefix.isBlank();
        boolean noType = type == null;
        boolean noMoveClass = moveClass == null;

        // Tenemos que crear nueva variable para poder hacer el .toLowerCase antes
        // Las entradas de las lambdas deben ser constantes, finales
        String searchPrefix = noPrefix ? null : prefix.trim().toLowerCase(Locale.ROOT);

        return mapMoves.values().stream()
                .filter(m -> noPrefix || (m.name() != null && m.name().toLowerCase().startsWith(searchPrefix)))
                .filter(m -> noType || (m.type() != null && m.type().equals(type)))
                .filter(m -> noMoveClass || (m.moveClass() != null && m.moveClass().equals(moveClass)))
                .toList();
    }

    @Override
    public @NonNull List<MovementData> findAll() {
        //Devolvemos .lista inmutable, para concurrencia -> foto fija, si se modifica después no cambia para esta vista
        return List.copyOf(mapMoves.values());
    }

    @Override
    public void save(@NonNull MovementData movementData) {
        if (movementData.id() != null) {
            mapMoves.put(movementData.id().trim().toLowerCase(Locale.ROOT), movementData);
        }
    }

    @Override
    public void saveAll(@NonNull List<MovementData> movementData) {
        if (movementData != null) {
            movementData.forEach(this::save);
        }
    }

}
