package tennisboard.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMatchRequest(
        @NotBlank(message = "Player name not be null and must contain at least one non-whitespace character")
        @Size(min = 2, max = 100, message = "Player name must be between {min} and {max} characters")
        String firstPlayerName,

        @NotBlank(message = "Player name not be null and must contain at least one non-whitespace character")
        @Size(min = 2, max = 100, message = "Player name must be between {min} and {max} characters")
        String secondPlayerName) {
}
