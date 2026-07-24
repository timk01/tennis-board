package tennisboard.service.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum GamePoints {
    LOVE(0, 0),
    FIFTEEN(1, 15),
    THIRTY(2, 30),
    FORTY(3, 40);

    private final int round;
    private final int point;

    public static int getPointByRound(int round) {
        return Arrays.stream(GamePoints.values())
                .filter(p -> p.getRound() == round)
                .map(point -> point.getPoint())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Wrong round!"));
    }
}
