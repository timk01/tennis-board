package tennisboard.storage;

import tennisboard.service.logic.Match;

import java.util.Optional;
import java.util.UUID;

public interface OngoingMatchesStorage {
    void save(Match match);

    Optional<Match> findById(UUID uuid);

    void remove(UUID uuid, Match match);
}
