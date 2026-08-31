package tennisboard.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMatchRequest(
        @Size(min = 2, max = 100, message = "Player name must be between {min} and {max} characters")
        @NotBlank(message = "Player name must not be null and must contain at least one non-whitespace character")
        String name
) {
}
