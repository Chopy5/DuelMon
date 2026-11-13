package gal.usc.etse.es.servicios.model.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.model.enums.PrimaryStatusType;
import gal.usc.etse.es.servicios.model.enums.SideConditionType;
import gal.usc.etse.es.servicios.model.enums.StatType;
import gal.usc.etse.es.servicios.model.enums.Target;
import gal.usc.etse.es.servicios.model.enums.VolatileStatusType;
import org.jspecify.annotations.Nullable;

import java.util.List;

// Interfaz que solo pueden implementar las clases internas de MoveEffect -> para proteger
// Los records no pueden heredar de otras clases porque ya extienden record, pero sí pueden implementar interfaces
// SI lo hiciesemos con clases normales tendriamos que definir para todas con lombok, y total como van a ser solo lectura e inmutables, lo dejamos con records

// Elige la clase de movimiento según el valor del campo "type" en el JSON
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")    // En este caso va en funcion de un "name" que le damos, pero podria ser en funcion de CLASS comleta, o SIMPLE_NAME,..
@JsonSubTypes({
        @JsonSubTypes.Type(value = MoveEffect.StatChange.class,     name = "stat-change"),
        @JsonSubTypes.Type(value = MoveEffect.PrimaryStatus.class,  name = "primary-status"),
        @JsonSubTypes.Type(value = MoveEffect.VolatileStatus.class, name = "volatile-status"),
        @JsonSubTypes.Type(value = MoveEffect.Flinch.class,         name = "flinch"),
        @JsonSubTypes.Type(value = MoveEffect.MultiHit.class,       name = "multi-hit"),
        @JsonSubTypes.Type(value = MoveEffect.Recoil.class,         name = "recoil"),
        @JsonSubTypes.Type(value = MoveEffect.Drain.class,          name = "drain"),
        @JsonSubTypes.Type(value = MoveEffect.Heal.class,           name = "heal"),
        @JsonSubTypes.Type(value = MoveEffect.FixedDamage.class,    name = "fixed-damage"),
        @JsonSubTypes.Type(value = MoveEffect.SideCondition.class,  name = "side-condition"),
        @JsonSubTypes.Type(value = MoveEffect.Ohko.class,           name = "ohko"),
        @JsonSubTypes.Type(value = MoveEffect.HighCrit.class,       name = "high-crit"),
        @JsonSubTypes.Type(value = MoveEffect.Charge.class,         name = "charge"),
        @JsonSubTypes.Type(value = MoveEffect.Recharge.class,       name = "recharge"),
        @JsonSubTypes.Type(value = MoveEffect.SelfFaint.class,      name = "self-faint")
})
public interface MoveEffect {
    // 1. Modificación de estadísticas (ej. Screech: stat=DEF, stages=-2, chance=100, target=FOE)
    record StatChange(
            StatType stat,
            int stages,
            int chance,
            Target target
    ) implements MoveEffect {}

    // 2. Estado primario (ej. Flamethrower: status=BURN, chance=10, immuneTypes=[FIRE])
    record PrimaryStatus(
            PrimaryStatusType status,
            int chance,
            @Nullable List<PokemonType> immuneTypes
    ) implements MoveEffect {}

    // 3. Estado volátil (ej. Confuse Ray: status=CONFUSION, chance=100)
    record VolatileStatus(
            VolatileStatusType status,
            int chance,
            @Nullable List<PokemonType> immuneTypes
    ) implements MoveEffect {}

    // 4. Retroceso / amedrentar (ej. Rock Slide, Bite: chance=30)
    record Flinch(
            int chance
    ) implements MoveEffect {}

    // 5. Golpes múltiples (ej. Pin Missile: minHits=2, maxHits=5; Bonemerang: minHits=2, maxHits=2)
    record MultiHit(
            int minHits,
            int maxHits
    ) implements MoveEffect {}

    // 6. Daño por retroceso al usuario (ej. Double-Edge: fraction=0.33; Submission: fraction=0.25)
    record Recoil(
            double fraction
    ) implements MoveEffect {}

    // 7. Drenaje de salud al rival (ej. Mega Drain, Leech Life: fraction=0.5)
    record Drain(
            double fraction
    ) implements MoveEffect {}

    // 8. Curación propia (ej. Recover, Soft-Boiled: fraction=0.5)
    record Heal(
            double fraction
    ) implements MoveEffect {}

    // 9. Daño fijo (ej. Dragon Rage: value="40"; Night Shade: value="level", immuneTypes=[NORMAL])
    record FixedDamage(
            String value,
            @Nullable List<PokemonType> immuneTypes
    ) implements MoveEffect {}

    // 10. Pantallas o barreras de bando (ej. Light Screen, Reflect: condition=REFLECT, duration=5)
    record SideCondition(
            SideConditionType condition,
            int duration
    ) implements MoveEffect {}

    // 11. K.O. Fulminante de un golpe (ej. Fissure: immuneTypes=[FLYING]; Guillotine: immuneTypes=[GHOST])
    record Ohko(
            @Nullable List<PokemonType> immuneTypes
    ) implements MoveEffect {}

    // 12. Ratio de crítico aumentado (ej. Slash, Crabhammer, Karate Chop)
    record HighCrit() implements MoveEffect {}

    // 13. Movimiento con turno de carga (ej. Solar Beam, Sky Attack)
    record Charge() implements MoveEffect {}

    // 14. Turno de recarga posterior (ej. Hyper Beam)
    record Recharge() implements MoveEffect {}

    // 15. Sacrificio del usuario al atacar (ej. Explosion, Self-Destruct)
    record SelfFaint() implements MoveEffect {}
}
