package gal.usc.etse.es.servicios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Stats (
    int hp,
    int atk,
    int def,
    int spa,
    int spd,
    int spe
) {}
