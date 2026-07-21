package tennisboard.entity;

import jakarta.persistence.*;

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

    @Column(name = "name")
    private String name;

    public PlayerEntity() {
    }

    public PlayerEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
