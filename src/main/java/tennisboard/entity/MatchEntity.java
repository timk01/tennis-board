package tennisboard.entity;

public class MatchEntity {
    private int id;
    private int player1;
    private int player2;
    private int winner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayer1() {
        return player1;
    }

    public void setPlayer1(int player1) {
        this.player1 = player1;
    }

    public int getPlayer2() {
        return player2;
    }

    public void setPlayer2(int player2) {
        this.player2 = player2;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
}

/*
ID	Int	Первичный ключ, автоинкремент
Player1	Int	Айди первого игрока, внешний ключ на Players.ID
Player2	Int	Айди второго игрока, внешний ключ на Players.ID
Winner	Int	Айди победителя, внешний ключ на Players.ID
 */