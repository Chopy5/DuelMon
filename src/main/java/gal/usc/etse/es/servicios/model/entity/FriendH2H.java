package gal.usc.etse.es.servicios.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
public class FriendH2H {

    @Column(nullable = false)
    private int user1Wins = 0;

    @Column(nullable = false)
    private int user2Wins = 0;

    public void addWin(UUID winnerId, UUID user1Id) {
        if (winnerId.equals(user1Id)) {
            this.user1Wins++;
        } else {
            this.user2Wins++;
        }
    }
}

