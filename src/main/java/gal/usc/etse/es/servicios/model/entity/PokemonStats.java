package gal.usc.etse.es.servicios.model.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record PokemonStats(
    int hp,
    int atk,
    int def,
    int spa,
    int spd,
    int spe
) {}
