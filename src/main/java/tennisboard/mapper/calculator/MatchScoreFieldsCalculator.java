package tennisboard.mapper.calculator;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.StatusOfGame;
import tennisboard.service.logic.StatusOfSet;

@Component
public class MatchScoreFieldsCalculator {

    @Named("firstPlayerPoints")
    public String getFirstPlayerPoints(Match match) {
        StatusOfSet currentStatusOfSet = match.getMatchScore().getStatusOfSet();
        StatusOfGame currentStatusOfGame = match.getMatchScore().getStatusOfGame();

        String firstPlayerPoints;

        if (currentStatusOfSet == StatusOfSet.TIE_BREAK) {
            firstPlayerPoints = null;
        } else if (currentStatusOfGame == StatusOfGame.ADVANTAGE_A) {
            firstPlayerPoints = "AD";
        } else if (currentStatusOfGame == StatusOfGame.ADVANTAGE_B) {
            firstPlayerPoints = "40";
        } else {
            firstPlayerPoints = String.valueOf(match.getMatchScore().getPointA());
        }

        return firstPlayerPoints;
    }

    @Named("secondPlayerPoints")
    public String getSecondPlayerPoints(Match match) {
        StatusOfSet currentStatusOfSet = match.getMatchScore().getStatusOfSet();
        StatusOfGame currentStatusOfGame = match.getMatchScore().getStatusOfGame();

        String secondPlayerPoints;

        if (currentStatusOfSet == StatusOfSet.TIE_BREAK) {
            secondPlayerPoints = null;
        } else if (currentStatusOfGame == StatusOfGame.ADVANTAGE_A) {
            secondPlayerPoints = "40";
        } else if (currentStatusOfGame == StatusOfGame.ADVANTAGE_B) {
            secondPlayerPoints = "AD";
        } else {
            secondPlayerPoints = String.valueOf(match.getMatchScore().getPointB());
        }

        return secondPlayerPoints;
    }

    @Named("winnerName")
    public String getWinnerName(Match match) {
        return match.isFinished() ? match.getWinner().getName() : null;
    }
}
