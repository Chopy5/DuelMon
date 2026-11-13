package gal.usc.etse.es.servicios.model.dto;

import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;
import gal.usc.etse.es.servicios.model.entity.PokemonStats;
import gal.usc.etse.es.servicios.model.enums.PokemonType;

import java.util.List;

// DTO para devolver información de un pokemon, incluyendo los sprites
public record PokemonResponse(
        String id,
        int number,
        String name,
        PokemonType primaryType,
        PokemonType secondaryType,
        PokemonStats stats,
        List<String> moves, // Metemos solo los IDs de los movimientos
        Sprite sprites
) {
    public static PokemonResponse from(PokemonData entity, String spriteUrl) {

        // Pasamos solo los IDs de los movimientos, no la tabla completa
        List<String> moveIds = entity.getLearnset().stream()
                .map(MovementData::getId)
                .toList();

        return new PokemonResponse(
                entity.getId(),
                entity.getNumber(),
                entity.getName(),
                entity.getPrimaryType(),
                entity.getSecondaryType(),
                entity.getStats(),
                moveIds,
                Sprite.from(spriteUrl,entity.getNumber())
        );
    }
}