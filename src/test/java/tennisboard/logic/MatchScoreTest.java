package tennisboard.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchScoreTest {
    private Player first;
    private Player second;
    private MatchScore matchScore;
    private Match match;

    @BeforeEach
    void init() {
        first = new Player(1L, "Agasssi");
        second = new Player(2L, "Federerr");
        matchScore = new MatchScore();
        match = new Match(
                this.first,
                this.second,
                matchScore,
                false
        );
    }

    @Test
    void checkInitialScores() {
        int resultA = match.getMatchScore().getCurrentPointA();
        int resultB = match.getMatchScore().getCurrentPointB();

        assertThat(resultA).isEqualTo(0);
        assertThat(resultB).isEqualTo(0);

        assertThat(match.getMatchScore().getCurrentGameA()).isEqualTo(0);
        assertThat(match.getMatchScore().getCurrentGameB()).isEqualTo(0);

        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.REGULAR_GAME);

        //2 сета здесь
/*        assertThat(resultB).isEqualTo(0);
        assertThat(resultB).isEqualTo(0);*/
    }

    @Test
    void increasePointThrowsExceptionDueToNullValue() {
        assertThatThrownBy(() -> matchScore.increasePoint(null))
                .hasMessage("Side should be not null")
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void increasePointTo15ForSideAWorks() {
        match.getMatchScore().increasePoint(Side.A);

        assertThat(match.getMatchScore().getCurrentPointA()).isEqualTo(15);
    }

    @Test
    void increasePointTo15ForSideBWorks() {
        match.getMatchScore().increasePoint(Side.B);

        assertThat(match.getMatchScore().getCurrentPointB()).isEqualTo(15);
    }

    @Test
    void increasePointTo30ForSideAWorks() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        assertThat(match.getMatchScore().getCurrentPointA()).isEqualTo(30);
    }

    @Test
    void increasePointTo30ForSideBWorks() {
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        assertThat(match.getMatchScore().getCurrentPointB()).isEqualTo(30);
    }

    @Test
    void increasePointTo40ForSideAWorks() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        assertThat(match.getMatchScore().getCurrentPointA()).isEqualTo(40);
    }

    @Test
    void increasePointTo40ForSideBWorks() {
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        assertThat(match.getMatchScore().getCurrentPointB()).isEqualTo(40);
    }

    @Test
    void increasePointTo40ForSideADoesNotChangeBPoints() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        assertThat(match.getMatchScore().getCurrentPointA()).isEqualTo(40);
        assertThat(match.getMatchScore().getCurrentPointB()).isEqualTo(0);
    }

    @Test
    void increasePointTo40ForSideBDoesNotChangeAPoints() {
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        assertThat(match.getMatchScore().getCurrentPointB()).isEqualTo(40);
        assertThat(match.getMatchScore().getCurrentPointA()).isEqualTo(0);
    }

    @Test
    void checkFinalResultAfter40PointsForSideAWith0PointsForSideB() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(gameResultA).isEqualTo(1);
        assertThat(gameResultB).isEqualTo(0);
        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
    }

    @Test
    void checkFinalResultAfter40PointsForSideBWith0PointsForSideA() {
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(1);
        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
    }

    @Test
    void checkFinalResultAfter40PointsForSideAWith30PointsForSideB() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.A);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(gameResultA).isEqualTo(1);
        assertThat(gameResultB).isEqualTo(0);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.REGULAR_GAME);
        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
    }

    @Test
    void checkFinalResultAfter40PointsForSideBWith30PointsForSideA() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(1);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.REGULAR_GAME);
        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
    }

    @Test
    void checkIntermediateResultAfter40PointsForBothSides() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(40);
        assertThat(pointResultB).isEqualTo(40);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.DEUCE);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(0);
    }

    @Test
    void checkIntermediateResultAfterAdvantageForSideA() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.A);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(40);
        assertThat(pointResultB).isEqualTo(40);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.ADVANTAGE_A);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(0);
    }

    @Test
    void checkIntermediateResultAfterAdvantageForSideB() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(40);
        assertThat(pointResultB).isEqualTo(40);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.ADVANTAGE_B);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(0);
    }

    @Test
    void checkFinalResultAfterAdvantageForSideAWithAdditionalGoal() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.REGULAR_GAME);
        assertThat(gameResultA).isEqualTo(1);
        assertThat(gameResultB).isEqualTo(0);
    }

    @Test
    void checkFinalResultAfterAdvantageForSideBWithAdditionalGoal() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.REGULAR_GAME);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(1);
    }

    @Test
    void checkIntermediateResultAfterAdvantageForSideAAndDeuceAfterwards() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(40);
        assertThat(pointResultB).isEqualTo(40);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.DEUCE);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(0);
    }

    @Test
    void checkIntermediateResultAfterAdvantageForSideBAndDeuceAfterwards() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.A);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(40);
        assertThat(pointResultB).isEqualTo(40);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.DEUCE);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(0);
    }

    @Test
    void checkIntermediateResultAfterDeuceAdvantageDeuce() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.DEUCE);

        match.getMatchScore().increasePoint(Side.A);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(40);
        assertThat(pointResultB).isEqualTo(40);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.ADVANTAGE_A);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(0);

        match.getMatchScore().increasePoint(Side.B);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.DEUCE);
    }

    @Test
    void checkNextGamePointAfterFinishedOne() {
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);
        match.getMatchScore().increasePoint(Side.A);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        match.getMatchScore().increasePoint(Side.B);
        match.getMatchScore().increasePoint(Side.B);

        int gameResultA = match.getMatchScore().getCurrentGameA();
        int gameResultB = match.getMatchScore().getCurrentGameB();
        int pointResultA = match.getMatchScore().getCurrentPointA();
        int pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(0);
        assertThat(pointResultB).isEqualTo(0);
        assertThat(match.getMatchScore().getGameStatus()).isEqualTo(GameStatus.REGULAR_GAME);
        assertThat(gameResultA).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(1);

        match.getMatchScore().increasePoint(Side.A);

        gameResultB = match.getMatchScore().getCurrentGameB();
        pointResultA = match.getMatchScore().getCurrentPointA();
        pointResultB = match.getMatchScore().getCurrentPointB();

        assertThat(pointResultA).isEqualTo(15);
        assertThat(pointResultB).isEqualTo(0);
        assertThat(gameResultB).isEqualTo(1);
    }
}