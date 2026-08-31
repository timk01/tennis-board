package tennisboard.repository;

import tennisboard.entity.MatchEntity;

import java.util.List;

public interface MatchRepository {
    MatchEntity save(MatchEntity matchEntity);

    List<MatchEntity> findAllMatchesFiltered(int offset, int limit);

    List<MatchEntity> findAllMatchesByPlayerNameFiltered(int offset, int limit, String playerName);

    Long countMatchesPlayedByPlayer(String playerName);
    Long countAllMatches();
}
