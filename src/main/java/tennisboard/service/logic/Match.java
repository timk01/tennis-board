package tennisboard.service.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Match {
    private final UUID matchId;
    private final Player player1;
    private final Player player2;
    private final MatchScore matchScore;

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
