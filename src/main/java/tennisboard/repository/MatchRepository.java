package tennisboard.repository;

import org.springframework.stereotype.Repository;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.service.logic.Player;

import java.util.List;

public interface MatchRepository {
    List<MatchEntity> findAll();

    MatchEntity save(MatchEntity match);
}
