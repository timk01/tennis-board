package tennisboard.logic;

public class Match {
    private Player player1;
    private Player player2;
    private MatchScore matchScore;
    private boolean isFinished;

    public Match(Player player1, Player player2, MatchScore matchScore, boolean isFinished) {
        this.player1 = player1;
        this.player2 = player2;
        this.matchScore = matchScore;
        this.isFinished = isFinished;
    }

    public MatchScore getMatchScore() {
        return matchScore;
    }
}
