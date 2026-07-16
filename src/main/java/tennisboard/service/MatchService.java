package tennisboard.service;

import org.springframework.stereotype.Service;
import tennisboard.exception.MatchIsNotFoundException;
import tennisboard.exception.MatchValidationException;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.Optional;
import java.util.UUID;

@Service
public class MatchService {

    private final OngoingMatchesStorage ongoingMatchesStorage;

    public MatchService(OngoingMatchesStorage ongoingMatchesStorage) {
        this.ongoingMatchesStorage = ongoingMatchesStorage;
    }

    public UUID createNewMatch(String firstPlayerName, String secondPlayerName) {
        if (firstPlayerName == null || secondPlayerName == null || firstPlayerName.isBlank() || secondPlayerName.isBlank()) {
            throw new MatchValidationException(String.format(
                    "FirstName %s or/and SecondName %s cannot be null or empty", firstPlayerName, secondPlayerName
            ));
        }

        if (firstPlayerName.trim().equalsIgnoreCase(secondPlayerName.trim())) {
            throw new MatchValidationException(
                    "Names are the same!"
            );
        }


        UUID id = UUID.randomUUID();
        Match match = new Match(
                id,
                new Player(null, firstPlayerName),
                new Player(null, secondPlayerName),
                new MatchScore()
        );

        ongoingMatchesStorage.save(match);

        return id;
    }

    public Match getMatch(UUID uuid) {
        if (uuid == null) {
            throw new MatchValidationException(
                    "ID cannot be null");
        }

        Optional<Match> optionalMatch = ongoingMatchesStorage.findById(uuid);
        if (optionalMatch.isEmpty()) {
            throw new MatchIsNotFoundException(String.format(
                    "Cannot find match with ID: %s", uuid
            ));
        }

        return optionalMatch.get();
    }
}
