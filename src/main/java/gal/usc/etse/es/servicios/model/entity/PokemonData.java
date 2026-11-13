package gal.usc.etse.es.servicios.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "species")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PokemonData {
    @Id
    @EqualsAndHashCode.Include
    @Column(length = 50)
    private String id;

    @Column(nullable = false, unique = true)
    private int number;

    @Column(nullable = false,length = 50)
    private String name;

    // EnumType.STRING para almacenar el nombre del enum en la base de datos en vez de su indice
    // Se podria incluso usar un converter para almacenar el nombre slug, pero no nos vamos a rayar??????
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_type", nullable = false) // No usar - en nombres de tablas en sql, son la resta
    private PokemonType primaryType;

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(name = "secondary_type")
    private PokemonType secondaryType;

    @Embedded private PokemonStats stats;

    // LinkedHashSet para mantener el orden de inserción (por si quisiéramos que fuese importante)
    // El problema es que hibernate cuando guarda una lista la trata como un "bag", sin orden
    // Entonces para borrar un elemento, por ejemplo, tiene que borrar todos los elementos y volver a insertar los que quedan
    // Con un set lo hace inmediato

    // De cara al DTO, usaremos siempre un List, que es lo que espera el cliente, en un mismo orden siempre
    // Esto es porque en un Set, aunque LinkedHashSet sí lo haga, la interfaz no especifica que mantenga el orden
    // Entonces el cliente no sabrá si el orden es relevante o no, con una lista sí

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "species_moves", // Nombre de la tabla de union
            joinColumns = @JoinColumn(name = "species_id"), // Nombres de atributos FK de la tabla
            inverseJoinColumns = @JoinColumn(name = "move_id")
    )
    // El propio Hibernate ya detecta que se hace con MovementData -> no hace falta mencionarlo en esta otra

    private Set<MovementData> learnset = new LinkedHashSet<>();

    // Constructor completo para crear la especie la primera vez
    public PokemonData(String id, int number, String name, PokemonType primaryType,
                       @Nullable PokemonType secondaryType, PokemonStats stats,
                       Set<MovementData> learnset) {
        this.id = id;
        this.updateFrom(number, name, primaryType, secondaryType, stats, learnset);
    }

    // Metodo de sincronizacion para actualizar datos desde el JSON si cambian -> por ahora no nos sirve
    public void updateFrom(int number, String name, PokemonType primaryType,
                           @Nullable PokemonType secondaryType, PokemonStats stats,
                           Set<MovementData> learnset) {
        this.number = number;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.stats = stats;

        // CLEAR y addALL?????
        // No reasignamos la lista -> hibernate para los ManyToMany y OneToMany crea sets especificos

        // reutilizamos e que nos crea el, si reasignaramos lo sobreescribiriamos
        this.learnset.clear();
        this.learnset.addAll(learnset);
    }
}
