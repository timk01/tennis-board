package tennisboard.dto;

public record ShortMatchInfoDTO(
        String firstPlayerName,
        String secondPlayerName,
        String winnerName
) {
}
