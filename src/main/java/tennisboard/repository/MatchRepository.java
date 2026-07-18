package tennisboard.repository;

import org.springframework.stereotype.Repository;
import tennisboard.entity.MatchEntity;

import java.util.List;

public interface MatchRepository {
    List<MatchEntity> findAll();
}
