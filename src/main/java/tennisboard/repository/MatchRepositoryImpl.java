package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.service.logic.Player;

import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<MatchEntity> findAll() {
        return List.of();
    }

    @Override
    public MatchEntity save(MatchEntity match) {
        em.persist(match);
        return match;
    }
}
