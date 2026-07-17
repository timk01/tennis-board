package tennisboard.storage;

import org.springframework.stereotype.Component;
import tennisboard.service.logic.Match;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OngoingMatches implements OngoingMatchesStorage {
    private final Map<UUID, Match> ongoingMatches = new ConcurrentHashMap<>();

    @Override
    public void save(Match match) {
        Match exist = ongoingMatches.putIfAbsent(match.getMatchId(), match);

        if (exist != null) {
            throw new IllegalStateException(String.format(
                    "Match with UUID %s already exists", match.getMatchId()
            ));
        }
    }

    @Override
    public Optional<Match> findById(UUID uuid) {
        return Optional.ofNullable(ongoingMatches.get(uuid));
    }

    @Override
    public void remove(UUID uuid, Match match) {
        ongoingMatches.remove(uuid, match);
    }
}
