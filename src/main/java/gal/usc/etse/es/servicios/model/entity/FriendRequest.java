package gal.usc.etse.es.servicios.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "friend_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private Trainer sender; // En Java manejas el objeto User

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id")
    private Trainer receiver;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public FriendRequest(Trainer sender, Trainer receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = Instant.now();
    }
}
