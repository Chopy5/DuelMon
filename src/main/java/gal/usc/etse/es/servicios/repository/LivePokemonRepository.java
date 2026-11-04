package gal.usc.etse.es.servicios.repository;

import gal.usc.etse.es.servicios.model.dto.PokemonType;
import gal.usc.etse.es.servicios.model.entity.PokemonData;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LivePokemonRepository implements PokemonRepository{
    // ConcurrentHashMap para que pueda atender varias peticiones a la vez sin perder la integridad
    private final Map<Integer, PokemonData> mapId = new ConcurrentHashMap<>();
    private final Map<String, PokemonData> mapName = new ConcurrentHashMap<>();

    @Override
    public Optional<PokemonData> findById(int id) {
        // Encapsulamos el resultado en un optional
        return Optional.ofNullable(mapId.get(id));
    }

    @Override
    public Optional<PokemonData> findByNameIgnoreCase(@NonNull String name) {
        return Optional.ofNullable(mapName.get(name.toLowerCase()));
    }

    @Override
    public List<PokemonData> searchFiltersIgnoreCase(String prefix, PokemonType type) {
        if ((prefix == null || prefix.isBlank()) && type == null) {
            return findAll();
        }

        boolean noPrefix = prefix == null || prefix.isBlank();
        boolean noType = type == null;

        // Tenemos que crear nueva variable porque las lambdas no pueden tener algo que se modifica de parametro
        String searchPrefix = noPrefix ? null : prefix.trim().toLowerCase();

        return mapId.values().stream()
                .filter(p -> noPrefix || (p.name() != null && p.name().toLowerCase().startsWith(searchPrefix)))
                .filter(p -> noType || (p.types() != null && p.types().contains(type)))
                .toList();
    }

    @Override
    public List<PokemonData> findAll() {
        //Devolvemos .lista inmutable, para concurrencia -> foto fija, si se modifica después no cambia para esta vista
        return List.copyOf(mapId.values());
    }

    @Override
    public void save(PokemonData pokemonData) {
        if (pokemonData != null) {
            mapId.put(pokemonData.id(), pokemonData);
            if (pokemonData.name() != null) {
                mapName.put(pokemonData.name().toLowerCase(), pokemonData);
            }
        }
    }

    @Override
    public void saveAll(List<PokemonData> pokemonData) {
        if (pokemonData != null) {
            pokemonData.forEach(this::save);
        }
    }
}
