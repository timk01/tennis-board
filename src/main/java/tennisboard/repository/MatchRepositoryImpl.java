package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennisboard.entity.MatchEntity;

import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<MatchEntity> findAll() {
        String query2 = """
                select m
                from MatchEntity m
                join fetch m.firstPlayer
                join fetch m.secondPlayer
                join fetch m.winner
                order by m.id
                """;

        return em.createQuery(query2, MatchEntity.class)
                .getResultList();
    }

    @Override
    public MatchEntity save(MatchEntity match) {
        em.persist(match);
        return match;
    }
}
