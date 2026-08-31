package tennisboard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "players_table",
        indexes = {@Index(name = "index_players_name", columnList = "name", unique = true)}
)

public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    public PlayerEntity(String name) {
        this.name = name;
    }
}
