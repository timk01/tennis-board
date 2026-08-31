package tennisboard.service.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GamePoints {

    LOVE(0),
    FIFTEEN(15),
    THIRTY(30),
    FORTY(40);

    private final int point;

    public static int getPointByRound(int round) {
        return switch (round) {
            case 0 -> LOVE.point;
            case 1 -> FIFTEEN.point;
            case 2 -> THIRTY.point;
            case 3 -> FORTY.point;
            default -> throw new IllegalArgumentException("Wrong round!");
        };
    }
}
