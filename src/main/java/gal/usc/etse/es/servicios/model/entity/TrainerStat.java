package gal.usc.etse.es.servicios.model.entity;


import gal.usc.etse.es.servicios.model.enums.BattleMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// COMO USAR CONSTRAINTS EN TABLAS


@Entity
@Table(name = "user_stats", uniqueConstraints = {
        // Evita que un usuario tenga dos registros para el mismo modo de juego
        @UniqueConstraint(columnNames = {"user_id", "battle_mode"})
})

// COMO USAR LOMBOK
@Getter
@Setter
@NoArgsConstructor
public class TrainerStat {

    // Aqui no usamos UUID, no va a haber endpoint, es clase interna para el servidor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "El modo de batalla no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "battle_mode", nullable = false)
    private BattleMode mode;

    @Min(0)
    @Column(name = "wins", nullable = false)
    private int wins;

    @Min(0)
    @Column(name = "losses", nullable = false)
    private int losses;

    @Min(0)
    @Column(name = "elo", nullable = false)
    private int elo = 1200;

    //OJO CON ESTOOOOO
    @NotNull(message = "El usuario asociado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Trainer trainer;

    public TrainerStat(Trainer trainer, BattleMode mode) {
        this.trainer = trainer;
        this.mode = mode;
        this.wins = 0;
        this.losses = 0;
        this.elo = 1200;
    }

    public void recordWin(int eloGained) {
        this.wins++;
        this.elo += eloGained;
    }

    public void recordLoss(int eloLost) {
        this.losses++;
        this.elo = Math.max(0, this.elo - eloLost);
    }

}
