package tennisboard.mapper.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MatchScoreFieldsCalculatorTest {
    private MatchScoreFieldsCalculator calculator;
    private MatchScore matchScore;
    private Match match;

    @BeforeEach
    void init() {
        UUID uuid = UUID.fromString("1d5e5fb4-5203-4933-8278-486f3d8db2ca");
        Player player1 = new Player(1L, "Agassi");
        Player player2 = new Player(2L, "Federerr");
        matchScore = new MatchScore();
        match = new Match(
                uuid,
                player1,
                player2,
                matchScore);
        calculator = new MatchScoreFieldsCalculator();
    }

    @Test
    void playerAHasNullPointsIfTieBreak() {
        reachTieBreak();

        String firstPlayerPoints = calculator.getFirstPlayerPoints(match);
        assertThat(firstPlayerPoints).isNull();
    }

    @Test
    void playerBHasNullPointsIfTieBreak() {
        reachTieBreak();

        String secondPlayerPoints = calculator.getSecondPlayerPoints(match);
        assertThat(secondPlayerPoints).isNull();
    }

    @Test
    void playerAHasADIfPlayerAHasAdvantage() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);

        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);

        matchScore.increasePoint(Side.A);

        String firstPlayerPoints = calculator.getFirstPlayerPoints(match);
        assertThat(firstPlayerPoints).isEqualTo("AD");
    }

    @Test
    void playerBHasADIfPlayerBAdvantage() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);

        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);

        matchScore.increasePoint(Side.B);

        String secondPlayerPoints = calculator.getSecondPlayerPoints(match);
        assertThat(secondPlayerPoints).isEqualTo("AD");
    }

    @Test
    void playerAHas40IfPlayerBHasAdvantage() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);

        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);

        matchScore.increasePoint(Side.B);

        String firstPlayerPoints = calculator.getFirstPlayerPoints(match);
        assertThat(firstPlayerPoints).isEqualTo("40");
    }

    @Test
    void playerBHas40IfPlayerAHasAdvantage() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);

        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);

        matchScore.increasePoint(Side.A);

        String secondPlayerPoints = calculator.getSecondPlayerPoints(match);
        assertThat(secondPlayerPoints).isEqualTo("40");
    }

    @Test
    void playerAHas30IfScoredCouplePoints() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);

        String firstPlayerPoints = calculator.getFirstPlayerPoints(match);
        assertThat(firstPlayerPoints).isEqualTo("30");
    }

    @Test
    void playerBHas30IfScoredCouplePoints() {
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);

        String secondPlayerPoints = calculator.getSecondPlayerPoints(match);
        assertThat(secondPlayerPoints).isEqualTo("30");
    }

    @Test
    void newMatchHasNoWinnerIfMatchIsStillPlayed() {
        assertThat(calculator.getWinnerName(match)).isNull();
    }

    private void reachTieBreak() {
        winFourGamesForSideA();
        winFourGamesForSideB();
        winOneGameForSideA();
        winOneGameForSideB();
        winOneGameForSideA();
        winOneGameForSideB();
    }

    private void winFourGamesForSideA() {
        winOneGameForSideA();
        winOneGameForSideA();
        winOneGameForSideA();
        winOneGameForSideA();
    }

    private void winFourGamesForSideB() {
        winOneGameForSideB();
        winOneGameForSideB();
        winOneGameForSideB();
        winOneGameForSideB();
    }

    private void winOneGameForSideA() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
    }

    private void winOneGameForSideB() {
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
    }
}