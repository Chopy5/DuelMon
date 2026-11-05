/*
package gal.usc.etse.es.servicios.model.entity;


import gal.usc.etse.es.servicios.model.User;
import gal.usc.etse.es.servicios.model.dto.BattleMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// COMO USAR CONSTRAINTS EN TABLAS
@Entity
@Table(name = "user_stats", uniqueConstraints = {
        // Evita que un usuario tenga dos registros para el mismo modo de juego
        @UniqueConstraint(columnNames = {"user_id", "mode"})
})

// COMO USAR LOMBOK
@Getter
@Setter
@NoArgsConstructor
public class UserStat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private BattleMode mode;

    @Column(name = "wins", nullable = false)
    private int wins;

    @Column(name = "losses", nullable = false)
    private int losses;

    @Column(name = "elo", nullable = false)
    private int elo = 1200;

    //OJO CON ESTOOOOO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserStat(User user, BattleMode mode) {
        this.user = user;
        this.mode = mode;
        this.wins = 0;
        this.losses = 0;
        this.elo = 1200;
    }

}*/
