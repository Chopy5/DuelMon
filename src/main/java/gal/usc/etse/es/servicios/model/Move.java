package gal.usc.etse.es.servicios.model;

import java.util.ArrayList;

public class Move {
    String name;
    PokeType type;

    int power;
    int accuracy;
    int pp;
    int priority;
    Target target;
    Difficulty difficulty;
    ArrayList<Effect> effects;
}
