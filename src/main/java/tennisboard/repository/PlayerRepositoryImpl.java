package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennisboard.entity.AuthorEntity;
import tennisboard.entity.PlayerEntity;

import java.util.Optional;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public PlayerEntity save(PlayerEntity player) {
        em.persist(player);
        return player;
    }

    @Override
    public Optional<PlayerEntity> findByName(String playerName) {
        String query = """
                select p
                from PlayerEntity p
                where p.name = :name
                """;
        return em.createQuery(query, PlayerEntity.class)
                .setParameter("name", playerName)
                .getResultList()
                .stream()
                .findFirst();
    }
}
