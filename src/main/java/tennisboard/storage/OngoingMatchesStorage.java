package tennisboard.storage;

import tennisboard.service.logic.Match;

import java.util.UUID;

public interface OngoingMatchesStorage {
    void save(Match match);

    Match findById(UUID uuid);

    void remove(UUID uuid);
}
