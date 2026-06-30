package tennisboard.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;

import static org.assertj.core.api.Assertions.*;

class MatchTest {
    private static final int MINIMUM_GAMES_FOR_WIN_SET = 6;

    private Player firstPlayer;
    private Player secondPlayer;
    private MatchScore matchScore;
    private Match match;

    @BeforeEach
    void init() {
        firstPlayer = new Player(1L, "Agasssi");
        secondPlayer = new Player(2L, "Federerr");
        matchScore = new MatchScore();
        match = new Match(
                this.firstPlayer,
                this.secondPlayer,
                matchScore);
    }

    @Test
    void newlyCreatedMatchHasTwoPlayers() {
        assertThat(match.getPlayer1()).isSameAs(firstPlayer);
        assertThat(match.getPlayer2()).isSameAs(secondPlayer);
    }

    @Test
    void newMatchHasNoWinnerAndThrowsException() {
        assertThat(match.isFinished()).isFalse();
        assertThatThrownBy(() -> match.getWinner())
                .hasMessage("Match isn't finished yet")
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void newMatchWinnerSideAReturnsAsFirstPlayer() {
        winOneSetForSideA();
        winOneSetForSideA();

        assertThat(match.isFinished()).isTrue();
        assertThat(match.getWinner()).isSameAs(firstPlayer);
    }

    @Test
    void newMatchWinnerSideBReturnsAsSecondPlayer() {
        winOneSetForSideB();
        winOneSetForSideB();

        assertThat(match.isFinished()).isTrue();
        assertThat(match.getWinner()).isSameAs(secondPlayer);
    }

    private void winOneSetForSideA() {
        for (int i = 0; i < MINIMUM_GAMES_FOR_WIN_SET; i++) {
            winOneGameForSideA();
        }
    }

    private void winOneSetForSideB() {
        for (int i = 0; i < MINIMUM_GAMES_FOR_WIN_SET; i++) {
            winOneGameForSideB();
        }
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