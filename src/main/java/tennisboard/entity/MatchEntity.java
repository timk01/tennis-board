package tennisboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1")
    private PlayerEntity firstPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2")
    private PlayerEntity secondPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner")
    private PlayerEntity winner;

    public MatchEntity() {
    }

    public MatchEntity(PlayerEntity firstPlayer, PlayerEntity secondPlayer, PlayerEntity winner) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.winner = winner;
    }

    public int getId() {
        return id;
    }

    public PlayerEntity getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(PlayerEntity firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public PlayerEntity getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(PlayerEntity secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public PlayerEntity getWinner() {
        return winner;
    }

    public void setWinner(PlayerEntity winner) {
        this.winner = winner;
    }
}
