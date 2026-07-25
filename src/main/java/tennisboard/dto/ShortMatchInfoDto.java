package tennisboard.dto;

public record ShortMatchInfoDto(
        String firstPlayerName,
        String secondPlayerName,
        String winnerName
) {
}
