package gal.usc.etse.es.servicios.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"trainer1_id", "trainer2_id"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer1_id")
    private Trainer trainer1; // En Java manejas el objeto User

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer2_id")
    private Trainer trainer2;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "user1Wins", column = @Column(name = "random_user1_wins")),
            @AttributeOverride(name = "user2Wins", column = @Column(name = "random_user2_wins"))
    })
    private FriendH2H randomH2H = new FriendH2H();

    // H2H Modo Teams
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "user1Wins", column = @Column(name = "teams_user1_wins")),
            @AttributeOverride(name = "user2Wins", column = @Column(name = "teams_user2_wins"))
    })
    private FriendH2H teamsH2H = new FriendH2H();

    public Friendship(Trainer a, Trainer b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Los usuarios no pueden ser nulos");
        }

        if (a.getId().equals(b.getId())) {
            throw new IllegalArgumentException("Un usuario no puede ser amigo de sí mismo");
        }

        // Garantiza que user1 siempre sea el de menor ID
        if (a.getId().compareTo(b.getId()) < 0) {
            this.trainer1 = a;
            this.trainer2 = b;
        } else {
            this.trainer1 = b;
            this.trainer2 = a;
        }
        this.createdAt = Instant.now();
    }

    // Constructor adicional que acepta una FriendRequest como entrada directa
    public Friendship(FriendRequest request) {
        this(request.getSender(), request.getReceiver());
    }

    // Funcion para registrar una victoria en el H2H correspondiente según el modo
    public void recordWin(UUID winnerId, GameMode mode) {
        FriendH2H target = (mode == GameMode.RANDOM) ? randomH2H : teamsH2H;
        target.addWin(winnerId, this.trainer1.getId());
    }


}

