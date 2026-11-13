package gal.usc.etse.es.servicios.model.entity;

import gal.usc.etse.es.servicios.model.enums.MoveClass;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.model.enums.Target;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Entity
@Table(name = "moves")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovementData {
    @Id
    @EqualsAndHashCode.Include
    @Column(length = 50)
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PokemonType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoveClass category;

    @Nullable
    @Column
    private Integer power;

    @Nullable
    @Column
    private Integer accuracy;

    @Column(nullable = false)
    private int pp;

    @Column(nullable = false)
    private int priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Target target;

    @Convert(converter = MoveEffectListConverter.class) // Necesitamos un conversor :(
    @Column(nullable = false)
    private List<MoveEffect> effects;

    // Constructor completo
    public MovementData(String id, String name, PokemonType type, MoveClass category,
                        Integer power, Integer accuracy, int pp, int priority,
                        Target target, List<MoveEffect> effects) {
        this.id = id;
        this.updateFrom(name, type, category, power, accuracy, pp, priority, target, effects);
    }


    // Metodo para actualizacion
    public void updateFrom(String name, PokemonType type, MoveClass category,
                           Integer power, Integer accuracy, int pp, int priority,
                           Target target, List<MoveEffect> effects) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.priority = priority;
        this.target = target;

        // Copiamos lista para que sea inmutable
        this.effects = List.copyOf(effects);
    }
}
