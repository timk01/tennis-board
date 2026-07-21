package tennisboard.repository;

import tennisboard.entity.PlayerEntity;

import java.util.Optional;

public interface PlayerRepository {

    PlayerEntity save(PlayerEntity player);

    Optional<PlayerEntity> findByName(String name);

}
