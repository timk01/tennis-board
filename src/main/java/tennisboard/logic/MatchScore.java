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

    private int[] currentSet = new int[]{1, 2, 3};
    private int currentSetA;
    private int currentSetB;
    private GameStatus gameStatus = GameStatus.REGULAR_GAME;

    public MatchScore() {
        this.currentGameA = 0;
        this.currentGameB = 0;
        this.currentSetA = 1;
        this.currentSetB = 1;
    }

    public void increasePoint(Side side) {
        if (side == null) {
            throw new IllegalArgumentException("Side should be not null");
        }

        if (side == Side.A) {
            roundA++;

            if (roundA >= 4 && (roundA - roundB >= 2)) {
                currentGameA++;
                resetGame();
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
                return;
            }

            if (roundB <= 3) {
                currentPointB = basicGamePoints.get(roundB);
            }
        }

        updateGameStatus();
    }

    private void resetGame() {
        gameStatus = GameStatus.REGULAR_GAME;
        roundA = 0;
        currentPointA = 0;
        roundB = 0;
        currentPointB = 0;
    }

    private void updateGameStatus() {
        if (roundA < 3 || roundB < 3) {
            gameStatus = GameStatus.REGULAR_GAME;
            return;
        }

        if (roundA == roundB) {
            gameStatus = GameStatus.DEUCE;
        } else if (roundA - roundB == 1) {
            gameStatus = GameStatus.ADVANTAGE_A;
        } else if (roundB - roundA == 1) {
            gameStatus = GameStatus.ADVANTAGE_B;
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

    public GameStatus getGameStatus() {
        return gameStatus;
    }
}
