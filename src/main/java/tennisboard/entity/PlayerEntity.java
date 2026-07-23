package tennisboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "players",
        indexes = {@Index(name = "index_players_name", columnList = "name", unique = true)}
)

public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Setter
    @Column(name = "name")
    private String name;

    public PlayerEntity(String name) {
        this.name = name;
    }
}
