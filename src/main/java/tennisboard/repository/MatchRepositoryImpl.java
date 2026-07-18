package tennisboard.repository;

import org.springframework.stereotype.Repository;
import tennisboard.entity.MatchEntity;

import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository {
    @Override
    public List<MatchEntity> findAll() {
        return List.of();
    }
}
