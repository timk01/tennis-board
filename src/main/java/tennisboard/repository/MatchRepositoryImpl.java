package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tennisboard.entity.MatchEntity;

import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional
    @Override
    public MatchEntity save(MatchEntity match) {
        em.persist(match);
        return match;
    }

    /**
     * Метод делает 2 выборки:
     * 1) вытаскивает айди матчей, фильтруя по имени игрока: если он участовал в матче,
     * он будет либо равен первому, либо второму игроку
     * <p>
     * 2) используя полученные айди матчей как фильтр (where m.id in :matchesId) где точно есть нужный игрок,
     * динамически подгружает связные энтити (join fetch m.firstPlayer), чтобы избежать повторных запросов к БД
     *
     * @param offset     = (page - 1) * PAGE_ELEMENTS_SIZE;
     *                   - количество записей, которые нужно пропустить (например для 3 страницы от = 10)
     * @param limit      = количество показанных матчей на 1 странице (=PAGE_ELEMENTS_SIZE)
     * @param playerName имя игрока, которого хотим получить
     * @return List<MatchEntity> - отфильтрованное с оффсетов + лимитом и игроком
     */
    @Override
    public List<MatchEntity> findAllMatchesByPlayerNameFiltered(int offset, int limit, String playerName) {
        String selectMatchesIdQuery = """
                select m.id
                from MatchEntity m
                where m.firstPlayer.name = :playerName OR m.secondPlayer.name = :playerName
                order by m.id
                """;

        List<Long> matchesId = em.createQuery(selectMatchesIdQuery, Long.class)
                .setParameter("playerName", playerName)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        if (matchesId.isEmpty()) {
            return List.of();
        }

        String matchesInfoQuery = """
                select m
                from MatchEntity m
                join fetch m.firstPlayer
                join fetch m.secondPlayer
                join fetch m.winner
                where m.id in :matchesId
                order by m.id
                """;

        return em.createQuery(matchesInfoQuery, MatchEntity.class)
                .setParameter("matchesId", matchesId)
                .getResultList();
    }

    @Override
    public List<MatchEntity> findAllMatchesFiltered(int offset, int limit) {
        String selectMatchesIdQuery = """
                select m.id
                from MatchEntity m
                order by m.id
                """;

        List<Long> matchesId = em.createQuery(selectMatchesIdQuery, Long.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        if (matchesId.isEmpty()) {
            return List.of();
        }

        String matchesInfoQuery = """
                select m
                from MatchEntity m
                join fetch m.firstPlayer
                join fetch m.secondPlayer
                join fetch m.winner
                where m.id in :matchesId
                order by m.id
                """;

        return em.createQuery(matchesInfoQuery, MatchEntity.class)
                .setParameter("matchesId", matchesId)
                .getResultList();
    }

    /**
     * считает количество матчей (count(m)), сыгранных игроком с фильтром по имени
     * (если он играл в матче, он либо равен первому, либо второму игроку)
     *
     * @param playerName имя игрока, что ищем
     * @return количество матчей (общее), сыгранное им
     */
    @Override
    public Long countMatchesPlayedByPlayer(String playerName) {
        String countMatchesIdQuery = """
                select count(m)
                from MatchEntity m
                where m.firstPlayer.name = :playerName OR m.secondPlayer.name = :playerName
                """;

        return em.createQuery(countMatchesIdQuery, Long.class)
                .setParameter("playerName", playerName)
                .getSingleResult();
    }

    @Override
    public Long countAllMatches() {
        String countMatchesIdQuery = """
                select count(m)
                from MatchEntity m
                """;

        return em.createQuery(countMatchesIdQuery, Long.class)
                .getSingleResult();
    }
}
