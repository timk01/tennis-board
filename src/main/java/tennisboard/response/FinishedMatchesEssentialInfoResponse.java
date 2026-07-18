package tennisboard.response;

import java.util.List;

public record FinishedMatchesEssentialInfoResponse(
        List<ShortMatchInfo> matches,
        int currentPage,
        int totalPages
) {
}


