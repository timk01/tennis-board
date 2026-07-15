package tennisboard.service;

import org.springframework.stereotype.Service;
import tennisboard.reposiory.HelloRepository;
import tennisboard.storage.OngoingMatchesStorage;

@Service
public class MatchService {

    private final OngoingMatchesStorage ongoingMatchesStorage;

    public MatchService(OngoingMatchesStorage ongoingMatchesStorage) {
        this.ongoingMatchesStorage = ongoingMatchesStorage;
    }

    public matchUUIDDTO createNewMatch() {
        //logic...
    }
}
