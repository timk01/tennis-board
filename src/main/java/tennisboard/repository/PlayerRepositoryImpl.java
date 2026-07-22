package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tennisboard.entity.PlayerEntity;
import tennisboard.exception.PlayerNameAlreadyExistsException;

import java.util.Optional;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional
    @Override
    public PlayerEntity save(PlayerEntity player) {
        try {
            em.persist(player);
            em.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new PlayerNameAlreadyExistsException(String.format(
                    "Cannot save player %s due to data integrity violation", player.getName()
            ));
        }
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
