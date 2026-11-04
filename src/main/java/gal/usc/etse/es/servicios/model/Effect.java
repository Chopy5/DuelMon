package gal.usc.etse.es.servicios.model;

import gal.usc.etse.es.servicios.model.dto.Example;
import gal.usc.etse.es.servicios.model.dto.Stat;

import java.util.List;

public class Effect {
    private EffectType type;

    // Campos compartidos por muchos efectos
    private int chance;
    private Target target;
    private int turns;

    // Stat
    private Stat stat;
    private int stages;

    // Status / Condition
    private Example.StatusCondition status;
    private String scope;
    private int duration;
    private List<String> immuneTypes;

    // MultiHit / Drain / Recoil
    private int min;
    private int max;
    private float fraction;

    // FixedDamage
    private int value;

    // Constructor vacío para JSON
    public Effect() {}

    // Getters y Setters...
}