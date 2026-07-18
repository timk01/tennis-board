package tennisboard.dto;

import java.util.List;

public record FinishedMatchesEssentialInfoDTO(
        List<ShortMatchInfoDTO> matches,
        int currentPage,
        int totalPages
) {
}


