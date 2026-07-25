package tennisboard.dto;

import java.util.List;

public record FinishedMatchesEssentialInfoDto(
        List<ShortMatchInfoDto> matches,
        int currentPage,
        int totalPages
) {
}


