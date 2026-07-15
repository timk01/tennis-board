package tennisboard.storage;

import org.springframework.stereotype.Component;
import tennisboard.service.logic.Match;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OngoingMatches implements OngoingMatchesStorage {
    private final Map<UUID, Match> ongoingMatches = new ConcurrentHashMap<>();


    @Override
    public void save(Match match) {
        ongoingMatches.put(match.getMatchId(), match);
    }

    @Override
    public Match findById(UUID uuid) {
        return ongoingMatches.get(uuid);
    }

    @Override
    public void remove(UUID uuid) {
        ongoingMatches.remove(uuid);
    }
}
