package tennisboard.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMatchRequest(
        @NotBlank(message = "Player name must not be null and must contain at least one non-whitespace character")
        String name
) {
}
