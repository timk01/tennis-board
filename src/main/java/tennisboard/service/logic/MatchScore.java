package tennisboard.service.logic;

import java.util.List;

public class MatchScore {
    private static final int MINIMUM_ROUNDS_FOR_WIN_GAME = 4;
    private static final int MINIMUM_ROUNDS_FOR_GETTING_GAME_ADVANTAGE = 1;
    private static final int MAXIMUM_NORMAL_ROUNDS = 3;
    private static final int MINIMUM_GAMES_FOR_WIN_SET = 6;
    private static final int SET_TIE_BREAK_START = 6;
    private static final int MINIMUM_ROUNDS_TO_WIN_TIE_BREAK = 7;
    private static final int MINIMUM_ADVANTAGE = 2;
    private static final int MINIMUM_SETS_TO_WIN = 2;
    private static final List<Integer> BASIC_GAME_POINTS = List.of(0, 15, 30, 40);

    private int pointA;
    private int pointB;
    private int roundA;
    private int roundB;

    private int gameA;
    private int gameB;
    private StatusOfGame statusOfGame = StatusOfGame.REGULAR_GAME;

    private int setA;
    private int setB;
    private StatusOfSet statusOfSet = StatusOfSet.REGULAR_SET;

    private boolean isMatchFinished;
    private Side winner;
    private Side loser;


    public void increasePoint(Side side) {
        if (side == null) {
            throw new IllegalArgumentException("Side should be not null");
        }

        if (isMatchFinished) {
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
                pointA = BASIC_GAME_POINTS.get(roundA);
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
                pointB = BASIC_GAME_POINTS.get(roundB);
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
                processMatchResult();
                resetGame();
                resetSet();
                resetTieBreak();
            }
        } else {
            roundB++;

            if (roundB >= MINIMUM_ROUNDS_TO_WIN_TIE_BREAK && (roundB - roundA >= MINIMUM_ADVANTAGE)) {
                gameB++;
                setB++;
                processMatchResult();
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
                processMatchResult();
                resetSet();
            }
        } else {
            if (isSetWon(gameB, gameA)) {
                setB++;
                processMatchResult();
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

    private boolean isSetWon(int gamesWon, int opponentGamesWon) {
        return gamesWon >= MINIMUM_GAMES_FOR_WIN_SET && (gamesWon - opponentGamesWon >= MINIMUM_ADVANTAGE);
    }

    private void processMatchResult() {
        if (setA == MINIMUM_SETS_TO_WIN) {
            isMatchFinished = true;
            winner = Side.A;
            loser = Side.B;
        }

        if (setB == MINIMUM_SETS_TO_WIN) {
            isMatchFinished = true;
            winner = Side.B;
            loser = Side.A;
        }
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

    public int getPointA() {
        return pointA;
    }

    public int getPointB() {
        return pointB;
    }

    public int getGameA() {
        return gameA;
    }

    public int getGameB() {
        return gameB;
    }

    public StatusOfGame getStatusOfGame() {
        return statusOfGame;
    }

    public int getSetA() {
        return setA;
    }

    public int getSetB() {
        return setB;
    }

    public StatusOfSet getStatusOfSet() {
        return statusOfSet;
    }

    public Side getWinner() {
        if (!isMatchFinished) {
            throw new IllegalStateException("Match isn't finished yet");
        }

        return winner;
    }

    public boolean isMatchFinished() {
        return isMatchFinished;
    }
}
