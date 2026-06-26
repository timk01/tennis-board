package tennisboard.logic;

import java.util.Optional;

public class Match {
    private Player player1;
    private Player player2;
    private MatchScore matchScore;

    public Match(Player player1, Player player2, MatchScore matchScore) {
        this.player1 = player1;
        this.player2 = player2;
        this.matchScore = matchScore;
    }

    public MatchScore getMatchScore() {
        return matchScore;
    }

    public boolean isFinished() {
        return getMatchScore().isMatchFinished();
    }

    public Player getWinner() {
        Side winner = matchScore.getWinner();
        if (winner == Side.A) {
            return player1;
        }

        return player2;
    }
}
