package tennisboard.response;

public record ShortMatchInfo(
        String firstPlayerName,
        String secondPlayerName,
        String winnerName
) {
}
