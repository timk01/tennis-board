package tennisboard.logic;

import java.util.List;

public class MatchScore {
    private List<Integer> basicGamePoints = List.of(0, 15, 30, 40);
    private int currentPointA;
    private int currentPointB;
    private int roundA = 0;
    private int roundB = 0;
    private int currentGameA;
    private int currentGameB;
    private int gameA = 0;
    private int gameB = 0;

    private int currentSetA;
    private int currentSetB;
    private StatusOfGame gameStatus = StatusOfGame.REGULAR_GAME;
    private StatusOfSet setStatus = StatusOfSet.REGULAR_SET;

    private int tieBreakA;
    private int tieBreakB;

    private boolean isMatchFinished;
    private Side winner;
    private Side loser;


    public MatchScore() {
        this.currentGameA = 0;
        this.currentGameB = 0;
        this.currentSetA = 0;
        this.currentSetB = 0;
    }

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

            if (roundA >= 4 && (roundA - roundB >= 2)) {
                currentGameA++;
                resetGame();
                processGameResult(side);
                return;
            }

            if (roundA <= 3) {
                currentPointA = basicGamePoints.get(roundA);
            }
        } else {
            roundB++;
            if (roundB >= 4 && (roundB - roundA >= 2)) {
                currentGameB++;
                resetGame();
                processGameResult(side);
                return;
            }

            if (roundB <= 3) {
                currentPointB = basicGamePoints.get(roundB);
            }
        }

        updateGameStatus();
    }

    private void processMatchResult() {
        if (currentSetA == 2) {
            isMatchFinished = true;
            winner = Side.A;
            loser = Side.B;
        }

        if (currentSetB == 2) {
            isMatchFinished = true;
            winner = Side.B;
            loser = Side.A;
        }
    }

    private void processGameResult(Side side) {
        if (side == Side.A) {
            gameA++;

            if (gameA == 6 && gameB == 6) {
                setStatus = StatusOfSet.TIE_BREAK;
                return;
            }

            if ((gameA >= 6 && gameA - gameB >= 2)) {
                currentSetA++;
                processMatchResult();
                resetSet();
            }
        } else {
            gameB++;

            if (gameA == 6 && gameB == 6) {
                setStatus = StatusOfSet.TIE_BREAK;
                return;
            }

            if ((gameB >= 6 && gameB - gameA >= 2)) {
                currentSetB++;
                processMatchResult();
                resetSet();
            }
        }
    }

    private void tieBreakGame(Side side) {
        if (side == Side.A) {
            roundA++;
            tieBreakA++;

            if (roundA >= 7 && (roundA - roundB >= 2)) {
                currentGameA++;
                currentSetA++;
                processMatchResult();
                resetGame();
                resetSet();
                resetTieBreaks();
            }
        } else {
            roundB++;
            tieBreakB++;

            if (roundB >= 7 && (roundB - roundA >= 2)) {
                currentGameB++;
                currentSetB++;
                processMatchResult();
                resetGame();
                resetSet();
                resetTieBreaks();
            }
        }
    }

    private void resetTieBreaks() {
        tieBreakA = 0;
        tieBreakB = 0;
        setStatus = StatusOfSet.REGULAR_SET;
    }

    private void resetSet() {
        gameA = 0;
        currentGameA = 0;
        gameB = 0;
        currentGameB = 0;
    }

    private void resetGame() {
        gameStatus = StatusOfGame.REGULAR_GAME;
        roundA = 0;
        currentPointA = 0;
        roundB = 0;
        currentPointB = 0;
    }

    private void updateGameStatus() {
        if (roundA < 3 || roundB < 3) {
            gameStatus = StatusOfGame.REGULAR_GAME;
            return;
        }

        if (roundA == roundB) {
            gameStatus = StatusOfGame.DEUCE;
        } else if (roundA - roundB == 1) {
            gameStatus = StatusOfGame.ADVANTAGE_A;
        } else if (roundB - roundA == 1) {
            gameStatus = StatusOfGame.ADVANTAGE_B;
        }
    }

    public int getCurrentPointA() {
        return currentPointA;
    }

    public int getCurrentPointB() {
        return currentPointB;
    }

    public int getCurrentGameA() {
        return currentGameA;
    }

    public int getCurrentGameB() {
        return currentGameB;
    }

    public StatusOfGame getGameStatus() {
        return gameStatus;
    }

    public int getCurrentSetA() {
        return currentSetA;
    }

    public int getCurrentSetB() {
        return currentSetB;
    }

    public StatusOfSet getStatusOfSet() {
        return setStatus;
    }

    public int getTieBreakA() {
        return tieBreakA;
    }

    public int getTieBreakB() {
        return tieBreakB;
    }

    public Side getWinner() {
        if (!isMatchFinished) {
            throw new IllegalStateException("match isn't finished yet");
        }

        return winner;
    }

    public boolean isMatchFinished() {
        return isMatchFinished;
    }
}
