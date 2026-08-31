package tennisboard.service.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tennisboard.exception.MatchAlreadyFinishedException;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Match {
    private final UUID matchId;
    private final Player player1;
    private final Player player2;
    private final MatchScore matchScore;

    public boolean isFinished() {
        return getMatchScore().isMatchFinished();
    }

    public Player getWinner() {
        return switch (matchScore.getWinner()) {
            case A -> player1;
            case B -> player2;
        };
    }

    public void increasePoint(Side side) {
        if (isFinished()) {
            throw new MatchAlreadyFinishedException(
                    "Match is already finished!"
            );
        }

        getMatchScore().increasePoint(side);
    }
}
