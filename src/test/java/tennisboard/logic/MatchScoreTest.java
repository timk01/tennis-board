package tennisboard.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Side;
import tennisboard.service.logic.StatusOfGame;
import tennisboard.service.logic.StatusOfSet;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchScoreTest {
    private MatchScore matchScore;

    @BeforeEach
    void init() {
        matchScore = new MatchScore();
    }

    @Test
    void checkNewScoreSettings() {
        assertThat(matchScore.getPointA()).isEqualTo(0);
        assertThat(matchScore.getPointB()).isEqualTo(0);

        assertThat(matchScore.getGameA()).isEqualTo(0);
        assertThat(matchScore.getGameB()).isEqualTo(0);
        assertThat(matchScore.getStatusOfGame()).isEqualTo(StatusOfGame.REGULAR_GAME);

        assertThat(matchScore.getSetA()).isEqualTo(0);
        assertThat(matchScore.getSetB()).isEqualTo(0);
        assertThat(matchScore.getStatusOfSet()).isEqualTo(StatusOfSet.REGULAR_SET);

        assertThat(matchScore.isMatchFinished()).isFalse();
    }

    @Test
    void increasePointThrowsExceptionDueToNullValue() {
        assertThatThrownBy(() -> matchScore.increasePoint(null))
                .hasMessage("Side should be not null")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("dataForSimplePointTest")
    void checkPointsDistributionIsCorrect(Side side, int methodCalls, int expectedPointsA, int expectedPointsB) {
        for (int i = 0; i < methodCalls; i++) {
            matchScore.increasePoint(side);
        }

        assertThat(matchScore.getPointA()).isEqualTo(expectedPointsA);
        assertThat(matchScore.getPointB()).isEqualTo(expectedPointsB);
    }

    private static Stream<Arguments> dataForSimplePointTest() {
        return Stream.of(
                Arguments.of(Side.A, 0, 0, 0),
                Arguments.of(Side.A, 1, 15, 0),
                Arguments.of(Side.A, 2, 30, 0),
                Arguments.of(Side.A, 3, 40, 0),

                Arguments.of(Side.B, 0, 0, 0),
                Arguments.of(Side.B, 1, 0, 15),
                Arguments.of(Side.B, 2, 0, 30),
                Arguments.of(Side.B, 3, 0, 40)
        );
    }

    @ParameterizedTest
    @MethodSource("mixedDataForRegularGameTest")
    void checkOneRegularGameVariants(
            List<Side> sides,
            int gameResultA,
            int gameResultB,
            StatusOfGame status,
            int pointResultA,
            int pointResultB) {
        for (Side side : sides) {
            matchScore.increasePoint(side);
        }

        assertThat(matchScore.getGameA()).isEqualTo(gameResultA);
        assertThat(matchScore.getGameB()).isEqualTo(gameResultB);
        assertThat(matchScore.getStatusOfGame()).isEqualTo(status);
        assertThat(matchScore.getPointA()).isEqualTo(pointResultA);
        assertThat(matchScore.getPointB()).isEqualTo(pointResultB);
    }

    private static Stream<Arguments> mixedDataForRegularGameTest() {
        StatusOfGame regularGame = StatusOfGame.REGULAR_GAME;
        return Stream.of(
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A),
                        1, 0, regularGame, 0, 0),
                Arguments.of(List.of(
                                Side.B, Side.B, Side.B, Side.B),
                        0, 1, regularGame, 0, 0),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.A),
                        1, 0, regularGame, 0, 0),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.B, Side.B, Side.B, Side.B),
                        0, 1, regularGame, 0, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("dataForDifferentStatusGameTest")
    void checkDifferentGameStatusVariants(
            List<Side> sides,
            int gameResultA,
            int gameResultB,
            StatusOfGame status,
            int pointResultA,
            int pointResultB) {
        for (Side side : sides) {
            matchScore.increasePoint(side);
        }

        assertThat(matchScore.getGameA()).isEqualTo(gameResultA);
        assertThat(matchScore.getGameB()).isEqualTo(gameResultB);
        assertThat(matchScore.getStatusOfGame()).isEqualTo(status);
        assertThat(matchScore.getPointA()).isEqualTo(pointResultA);
        assertThat(matchScore.getPointB()).isEqualTo(pointResultB);
    }

    private static Stream<Arguments> dataForDifferentStatusGameTest() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B),
                        0, 0, StatusOfGame.DEUCE, 40, 40),
                Arguments.of(List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B, Side.A),
                        0, 0, StatusOfGame.ADVANTAGE_A, 40, 40),
                Arguments.of(List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B, Side.B),
                        0, 0, StatusOfGame.ADVANTAGE_B, 40, 40),
                Arguments.of(List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B, Side.A, Side.A),
                        1, 0, StatusOfGame.REGULAR_GAME, 0, 0),
                Arguments.of(List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B, Side.B, Side.B),
                        0, 1, StatusOfGame.REGULAR_GAME, 0, 0),
                Arguments.of(List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B, Side.A, Side.B),
                        0, 0, StatusOfGame.DEUCE, 40, 40),
                Arguments.of(List.of(
                                Side.A, Side.A, Side.A, Side.B, Side.B, Side.B, Side.B, Side.A),
                        0, 0, StatusOfGame.DEUCE, 40, 40)
        );
    }

    @Test
    void checkIntermediateResultAfterDeuceAdvantageDeuce() {
        makeDeuceGame();

        assertThat(matchScore.getStatusOfGame()).isEqualTo(StatusOfGame.DEUCE);

        matchScore.increasePoint(Side.A);

        assertThat(matchScore.getPointA()).isEqualTo(40);
        assertThat(matchScore.getPointB()).isEqualTo(40);
        assertThat(matchScore.getStatusOfGame()).isEqualTo(StatusOfGame.ADVANTAGE_A);
        assertThat(matchScore.getGameA()).isEqualTo(0);
        assertThat(matchScore.getGameB()).isEqualTo(0);

        matchScore.increasePoint(Side.B);
        assertThat(matchScore.getStatusOfGame()).isEqualTo(StatusOfGame.DEUCE);
    }

    @Test
    void checkNextGamePointAfterFinishedOne() {
        makeDeuceGame();

        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);

        assertThat(matchScore.getPointA()).isEqualTo(0);
        assertThat(matchScore.getPointB()).isEqualTo(0);
        assertThat(matchScore.getStatusOfGame()).isEqualTo(StatusOfGame.REGULAR_GAME);
        assertThat(matchScore.getGameA()).isEqualTo(0);
        assertThat(matchScore.getGameB()).isEqualTo(1);

        matchScore.increasePoint(Side.A);

        assertThat(matchScore.getPointA()).isEqualTo(15);
        assertThat(matchScore.getPointB()).isEqualTo(0);
        assertThat(matchScore.getGameB()).isEqualTo(1);
    }

    private void makeDeuceGame() {
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);
        matchScore.increasePoint(Side.A);

        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
        matchScore.increasePoint(Side.B);
    }

    @ParameterizedTest
    @MethodSource("mixedDataForRegularSetTest")
    void checkOneRegularSetVariants(
            List<Side> gameWinnerSides,
            int gameResultA,
            int gameResultB,
            int setResultA,
            int setResultB) {
        winSets(gameWinnerSides);

        assertThat(matchScore.getGameA()).isEqualTo(gameResultA);
        assertThat(matchScore.getGameB()).isEqualTo(gameResultB);
        assertThat(matchScore.getSetA()).isEqualTo(setResultA);
        assertThat(matchScore.getSetB()).isEqualTo(setResultB);
    }

    private static Stream<Arguments> mixedDataForRegularSetTest() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.A),
                        0, 0, 1, 0),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B,
                                Side.B, Side.B),
                        0, 0, 0, 1),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.B,
                                Side.A, Side.A),
                        0, 0, 1, 0),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.B,
                                Side.B, Side.B),
                        0, 0, 0, 1)
        );
    }

    private void winSets(List<Side> gameWinnerSides) {
        for (Side gameWinner : gameWinnerSides) {
            if (gameWinner == Side.A) {
                winOneGameForSideA();
            } else {
                winOneGameForSideB();
            }
        }
    }

    @Test
    void testReachingTieBreak() {
        reachTieBreak();

        assertThat(matchScore.getStatusOfSet()).isEqualTo(StatusOfSet.TIE_BREAK);
    }

    private void reachTieBreak() {
        winFourGamesForSideA();
        winFourGamesForSideB();
        winOneGameForSideA();
        winOneGameForSideB();
        winOneGameForSideA();
        winOneGameForSideB();
    }

    @ParameterizedTest
    @MethodSource("tieBreakSetData")
    void checkTieBreakSetVariants(
            List<Side> scoringSides,
            int gameResultA,
            int gameResultB,
            int setResultA,
            int setResultB,
            StatusOfSet status) {
        reachTieBreak();

        for (Side scoringSide : scoringSides) {
            matchScore.increasePoint(scoringSide);
        }

        assertThat(matchScore.getGameA()).isEqualTo(gameResultA);
        assertThat(matchScore.getGameB()).isEqualTo(gameResultB);
        assertThat(matchScore.getSetA()).isEqualTo(setResultA);
        assertThat(matchScore.getSetB()).isEqualTo(setResultB);
        assertThat(matchScore.getStatusOfSet()).isEqualTo(status);
    }

    private static Stream<Arguments> tieBreakSetData() {
        StatusOfSet regular = StatusOfSet.REGULAR_SET;
        StatusOfSet tieBreak = StatusOfSet.TIE_BREAK;
        return Stream.of(
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A, Side.A, Side.A, Side.A),
                        0, 0, 1, 0, regular),
                Arguments.of(
                        List.of(
                                Side.B, Side.B, Side.B, Side.B, Side.B, Side.B, Side.B),
                        0, 0, 0, 1, regular),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B, Side.B,
                                Side.A
                        ),
                        0, 0, 1, 0, regular),
                Arguments.of(
                        List.of(
                                Side.B, Side.B, Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.A, Side.A, Side.A, Side.A,
                                Side.B
                        ),
                        0, 0, 0, 1, regular),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B, Side.B, Side.B,
                                Side.A
                        ),
                        6, 6, 0, 0, tieBreak),
                Arguments.of(
                        List.of(
                                Side.B, Side.B, Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.A, Side.A, Side.A, Side.A, Side.A,
                                Side.B
                        ),
                        6, 6, 0, 0, tieBreak),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.B,
                                Side.A, Side.A
                        ),
                        0, 0, 1, 0, regular),
                Arguments.of(
                        List.of(
                                Side.A, Side.A, Side.A, Side.A, Side.A, Side.A,
                                Side.B, Side.B, Side.B, Side.B, Side.B, Side.B,
                                Side.A, Side.B,
                                Side.B, Side.B
                        ),
                        0, 0, 0, 1, regular)
        );
    }

    @Test
    void overallTestAfterWinOnTieBreakAndIncreaseOnePoint() {
        reachTieBreak();

        for (int i = 0; i < 7; i++) {
            matchScore.increasePoint(Side.A);
        }

        assertThat(matchScore.getGameA()).isEqualTo(0);
        assertThat(matchScore.getGameB()).isEqualTo(0);
        assertThat(matchScore.getSetA()).isEqualTo(1);
        assertThat(matchScore.getSetB()).isEqualTo(0);
        assertThat(matchScore.getStatusOfSet()).isEqualTo(StatusOfSet.REGULAR_SET);

        matchScore.increasePoint(Side.A);

        assertThat(matchScore.getPointA()).isEqualTo(15);
        assertThat(matchScore.getPointB()).isEqualTo(0);
        assertThat(matchScore.getGameA()).isEqualTo(0);
        assertThat(matchScore.getGameB()).isEqualTo(0);
        assertThat(matchScore.getSetA()).isEqualTo(1);
        assertThat(matchScore.getSetB()).isEqualTo(0);
        assertThat(matchScore.getStatusOfSet()).isEqualTo(StatusOfSet.REGULAR_SET);
    }

    @Test
    void checkMatchHasNoWinnerIfEachWon1Set() {
        winOneSetForSideA();
        winOneSetForSideB();

        assertThat(matchScore.getSetA()).isEqualTo(1);
        assertThat(matchScore.getSetB()).isEqualTo(1);
        assertThat(matchScore.isMatchFinished()).isFalse();
    }

    private void winOneSetForSideA() {
        winFourGamesForSideA();
        winFourGamesForSideB();
        winOneGameForSideA();
        winOneGameForSideB();
        winOneGameForSideA();
        winOneGameForSideA();
    }

    private void winOneSetForSideB() {
        winFourGamesForSideA();
        winFourGamesForSideB();
        winOneGameForSideA();
        winOneGameForSideB();
        winOneGameForSideB();
        winOneGameForSideB();
    }

    @Test
    void checkMatchHasAWinnerIfOneWon2Sets() {
        winOneSetForSideA();
        winOneSetForSideA();

        assertThat(matchScore.getSetA()).isEqualTo(2);
        assertThat(matchScore.getSetB()).isEqualTo(0);
        assertThat(matchScore.isMatchFinished()).isTrue();
        assertThat(matchScore.getWinner()).isEqualTo(Side.A);
    }

    @Test
    void checkMatchHasAWinnerIfSecondWon2Sets() {
        winOneSetForSideB();
        winOneSetForSideB();

        assertThat(matchScore.getSetA()).isEqualTo(0);
        assertThat(matchScore.getSetB()).isEqualTo(2);
        assertThat(matchScore.isMatchFinished()).isTrue();
        assertThat(matchScore.getWinner()).isEqualTo(Side.B);
    }

    @Test
    void checkMatchHasAWinnerIfOneWon2SetsAndAnotherOnly1Set() {
        winOneSetForSideA();
        winOneSetForSideB();
        winOneSetForSideA();

        assertThat(matchScore.getSetA()).isEqualTo(2);
        assertThat(matchScore.getSetB()).isEqualTo(1);
        assertThat(matchScore.isMatchFinished()).isTrue();
    }

    @Test
    void checkMatchHasAWinnerIfSecondWon2SetsAndAnotherOnly1Set() {
        winOneSetForSideB();
        winOneSetForSideA();
        winOneSetForSideB();

        assertThat(matchScore.getSetA()).isEqualTo(1);
        assertThat(matchScore.getSetB()).isEqualTo(2);
        assertThat(matchScore.isMatchFinished()).isTrue();
    }

    @Test
    void checkPointsCanNotBeChangedAfterFinish() {
        winOneSetForSideA();
        winOneSetForSideA();

        assertThat(matchScore.getSetA()).isEqualTo(2);
        assertThat(matchScore.getSetB()).isEqualTo(0);
        assertThat(matchScore.isMatchFinished()).isTrue();

        winFourGamesForSideA();

        assertThat(matchScore.getPointA()).isEqualTo(0);
        assertThat(matchScore.getPointB()).isEqualTo(0);
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