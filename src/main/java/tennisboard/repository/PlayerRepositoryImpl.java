package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;
import tennisboard.entity.PlayerEntity;
import tennisboard.exception.PlayerNameAlreadyExistsException;

import java.util.Optional;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository {
    private static final String UNIQUE_VIOLATION = "23505";
    private static final String FIND_PLAYER_BY_NAME = """
            SELECT p
            FROM PlayerEntity p
            WHERE p.name = :name
            """;

    @PersistenceContext
    private EntityManager em;

    @Override
    public PlayerEntity save(PlayerEntity playerEntity) {
        try {
            em.persist(playerEntity);
            em.flush();
        } catch (PersistenceException e) {
            if (hasSqlState(e, UNIQUE_VIOLATION)) {
                throw new PlayerNameAlreadyExistsException(
                        String.format(
                                "Cannot save player %s due to persistence error",
                                playerEntity.getName()
                        ),
                        e
                );
            }

            throw e;
        }
        return playerEntity;
    }

    private static boolean hasSqlState(PersistenceException e, String sqlState) {
        Throwable cause = e;

        while (cause != null) {
            if (cause instanceof PSQLException psqlException &&
             sqlState.equals(psqlException.getSQLState())) {
                return true;
            }

            cause = cause.getCause();
        }
        return false;
    }

    @Override
    public Optional<PlayerEntity> findByName(String playerName) {
        return em.createQuery(FIND_PLAYER_BY_NAME, PlayerEntity.class)
                .setParameter("name", playerName)
                .getResultList()
                .stream()
                .findFirst();
    }
}