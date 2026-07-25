package tennisboard.service.logic;

import lombok.Getter;

import java.util.Optional;

public class MatchScore {
    private static final int MINIMUM_ROUNDS_FOR_WIN_GAME = 4;
    private static final int MINIMUM_ROUNDS_FOR_GETTING_GAME_ADVANTAGE = 1;
    private static final int MAXIMUM_NORMAL_ROUNDS = 3;
    private static final int MINIMUM_GAMES_FOR_WIN_SET = 6;
    private static final int SET_TIE_BREAK_START = 6;
    private static final int MINIMUM_ROUNDS_TO_WIN_TIE_BREAK = 7;
    private static final int MINIMUM_ADVANTAGE = 2;
    private static final int MINIMUM_SETS_TO_WIN = 2;

    @Getter
    private int pointA;

    @Getter
    private int pointB;

    private int roundA;

    private int roundB;

    @Getter
    private int gameA;

    @Getter
    private int gameB;

    @Getter
    private StatusOfGame statusOfGame = StatusOfGame.REGULAR_GAME;

    @Getter
    private int setA;

    @Getter
    private int setB;

    @Getter
    private StatusOfSet statusOfSet = StatusOfSet.REGULAR_SET;

    public void increasePoint(Side side) {
        if (side == null) {
            throw new IllegalArgumentException("Side should be not null");
        }

        if (isMatchFinished()) {
            return;
        }

        if (getStatusOfSet() == StatusOfSet.TIE_BREAK) {
            tieBreakGame(side);
            return;
        }

        if (side == Side.A) {
            roundA++;
            if (isRoundWon(roundA, roundB)) {
                gameA++;
                processGameResult(side);
                resetGame();
                return;
            }

            if (roundA <= MAXIMUM_NORMAL_ROUNDS) {
                pointA = GamePoints.getPointByRound(roundA);
            }
        } else {
            roundB++;
            if (isRoundWon(roundB, roundA)) {
                gameB++;
                processGameResult(side);
                resetGame();
                return;
            }

            if (roundB <= MAXIMUM_NORMAL_ROUNDS) {
                pointB = GamePoints.getPointByRound(roundB);
            }
        }

        updateGameStatus();
    }

    private void tieBreakGame(Side side) {
        if (side == Side.A) {
            roundA++;

            if (roundA >= MINIMUM_ROUNDS_TO_WIN_TIE_BREAK && (roundA - roundB >= MINIMUM_ADVANTAGE)) {
                gameA++;
                setA++;
                resetGame();
                resetSet();
                resetTieBreak();
            }
        } else {
            roundB++;

            if (roundB >= MINIMUM_ROUNDS_TO_WIN_TIE_BREAK && (roundB - roundA >= MINIMUM_ADVANTAGE)) {
                gameB++;
                setB++;
                resetGame();
                resetSet();
                resetTieBreak();
            }
        }
    }

    private void resetTieBreak() {
        statusOfSet = StatusOfSet.REGULAR_SET;
    }

    private boolean isRoundWon(int roundsWon, int opponentRoundsWon) {
        return roundsWon >= MINIMUM_ROUNDS_FOR_WIN_GAME && (roundsWon - opponentRoundsWon >= MINIMUM_ADVANTAGE);
    }

    private void processGameResult(Side side) {
        if (processTieBreakStart()) {
            return;
        }

        if (side == Side.A) {
            if (isSetWon(gameA, gameB)) {
                setA++;
                resetSet();
            }
        } else {
            if (isSetWon(gameB, gameA)) {
                setB++;
                resetSet();
            }
        }
    }

    private boolean processTieBreakStart() {
        if (gameA == SET_TIE_BREAK_START && gameB == SET_TIE_BREAK_START) {
            statusOfSet = StatusOfSet.TIE_BREAK;
            return true;
        }
        return false;
    }

    public Integer getTieBreakPointA() {
        return statusOfSet == StatusOfSet.TIE_BREAK ? roundA : null;
    }

    public Integer getTieBreakPointB() {
        return statusOfSet == StatusOfSet.TIE_BREAK ? roundB : null;
    }


    private boolean isSetWon(int gamesWon, int opponentGamesWon) {
        return gamesWon >= MINIMUM_GAMES_FOR_WIN_SET && (gamesWon - opponentGamesWon >= MINIMUM_ADVANTAGE);
    }

    private void resetGame() {
        statusOfGame = StatusOfGame.REGULAR_GAME;
        roundA = 0;
        pointA = 0;
        roundB = 0;
        pointB = 0;
    }

    private void resetSet() {
        gameA = 0;
        gameB = 0;
    }

    private void updateGameStatus() {
        if (roundA < MAXIMUM_NORMAL_ROUNDS || roundB < MAXIMUM_NORMAL_ROUNDS) {
            statusOfGame = StatusOfGame.REGULAR_GAME;
            return;
        }

        if (roundA == roundB) {
            statusOfGame = StatusOfGame.DEUCE;
        } else if (roundA - roundB == MINIMUM_ROUNDS_FOR_GETTING_GAME_ADVANTAGE) {
            statusOfGame = StatusOfGame.ADVANTAGE_A;
        } else if (roundB - roundA == MINIMUM_ROUNDS_FOR_GETTING_GAME_ADVANTAGE) {
            statusOfGame = StatusOfGame.ADVANTAGE_B;
        }
    }

    public Side getWinner() {
        return winningSide().orElseThrow(() -> new IllegalStateException("Match isn't finished yet"));
    }

    private Optional<Side> winningSide() {
        if (setA == MINIMUM_SETS_TO_WIN) {
            return Optional.of(Side.A);
        }

        if (setB == MINIMUM_SETS_TO_WIN) {
            return Optional.of(Side.B);
        }

        return Optional.empty();
    }

    public boolean isMatchFinished() {
        return winningSide().isPresent();
    }
}
