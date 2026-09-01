package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennisboard.entity.MatchEntity;

import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    private static final String SELECT_MATCH_IDS = """
            SELECT m.id
            FROM MatchEntity m
            """;

    private static final String SELECT_MATCH_IDS_BY_PLAYER_NAME =
            SELECT_MATCH_IDS + """
                    WHERE m.firstPlayer.name = :playerName
                       OR m.secondPlayer.name = :playerName
                    ORDER BY m.id DESC
                    """;

    private static final String SELECT_ALL_MATCH_IDS =
            SELECT_MATCH_IDS + """
            ORDER BY m.id DESC
            """;

    private static final String SELECT_MATCH_WITH_FETCH = """
            SELECT m
            FROM MatchEntity m
            JOIN FETCH m.firstPlayer
            JOIN FETCH m.secondPlayer
            JOIN FETCH m.winner
            """;

    private static final String SELECT_MATCH_INFO_BY_MATCHES_ID =
            SELECT_MATCH_WITH_FETCH + """
            WHERE m.id IN :matchesId
            ORDER BY m.id DESC
            """;

    private static final String COUNT_MATCHES_BY_PLAYER_NAME = """
            SELECT COUNT(m)
            FROM MatchEntity m
            WHERE m.firstPlayer.name = :playerName
               OR m.secondPlayer.name = :playerName
            """;

    private static final String COUNT_ALL_MATCHES = """
            SELECT COUNT(m)
            FROM MatchEntity m
            """;

    @PersistenceContext
    private EntityManager em;

    @Override
    public MatchEntity save(MatchEntity match) {
        em.persist(match);
        return match;
    }

    /**
     * Метод делает 2 выборки:
     * 1) вытаскивает айди матчей, фильтруя по имени игрока: если он участвовал в матче,
     * он будет либо равен первому, либо второму игроку
     * <p>
     * 2) используя полученные айди матчей как фильтр (where m.id in :matchesId),
     * динамически подгружает связанные энтити (join fetch m.firstPlayer),
     * чтобы избежать повторных запросов к БД
     *
     * @param offset     = (page - 1) * PAGE_ELEMENTS_SIZE;
     *                   количество записей, которые нужно пропустить
     * @param limit      количество показанных матчей на 1 странице (=PAGE_ELEMENTS_SIZE)
     * @param playerName имя игрока, которого хотим получить
     * @return отфильтрованный список матчей
     */
    @Override
    public List<MatchEntity> findAllMatchesByPlayerNameFiltered(
            int offset,
            int limit,
            String playerName
    ) {
        List<Long> matchesId = em.createQuery(SELECT_MATCH_IDS_BY_PLAYER_NAME, Long.class)
                .setParameter("playerName", playerName)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        if (matchesId.isEmpty()) {
            return List.of();
        }

        return em.createQuery(SELECT_MATCH_INFO_BY_MATCHES_ID, MatchEntity.class)
                .setParameter("matchesId", matchesId)
                .getResultList();
    }

    @Override
    public List<MatchEntity> findAllMatchesFiltered(int offset, int limit) {
        List<Long> matchesId = em.createQuery(SELECT_ALL_MATCH_IDS, Long.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        if (matchesId.isEmpty()) {
            return List.of();
        }

        return em.createQuery(SELECT_MATCH_INFO_BY_MATCHES_ID, MatchEntity.class)
                .setParameter("matchesId", matchesId)
                .getResultList();
    }

    /**
     * Считает количество матчей, сыгранных игроком с фильтром по имени.
     *
     * @param playerName имя игрока
     * @return количество сыгранных им матчей
     */
    @Override
    public Long countMatchesPlayedByPlayer(String playerName) {
        return em.createQuery(COUNT_MATCHES_BY_PLAYER_NAME, Long.class)
                .setParameter("playerName", playerName)
                .getSingleResult();
    }

    @Override
    public Long countAllMatches() {
        return em.createQuery(COUNT_ALL_MATCHES, Long.class)
                .getSingleResult();
    }
}