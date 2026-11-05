/*package gal.usc.etse.es.servicios.model;

import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.es.servicios.model.entity.UserStat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String hashPasswd;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String mail;

    @Column(name = "register_date", nullable = false, updatable = false)
    private Instant registerDate;

    @Column(name = "coins", nullable = false)
    private int coins = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserStat> stats = new ArrayList<>();



}
 */
