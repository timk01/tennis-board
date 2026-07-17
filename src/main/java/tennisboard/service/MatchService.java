package tennisboard.service;

import org.springframework.stereotype.Service;
import tennisboard.exception.MatchIsNotFoundException;
import tennisboard.exception.MatchValidationException;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.List;
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

        String normalizedFirstPlayerName = firstPlayerName.trim().toLowerCase();
        String normalizedSecondPlayerName = secondPlayerName.trim().toLowerCase();
        if (normalizedFirstPlayerName.equals(normalizedSecondPlayerName)) {
            throw new MatchValidationException(
                    "Names are the same!"
            );
        }

        UUID id = UUID.randomUUID();
        Match match = new Match(
                id,
                new Player(null, normalizedFirstPlayerName),
                new Player(null, normalizedSecondPlayerName),
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

    //toDo подумать про то, что вообще говоря.... проблеема многопоточки
    // это 3 места.
    //  POST /matches/{uuid}/point
    // GET /matches/{uuid} (где соираем конкретный матч)
    // ???

    //todo ЗДЕСЬ ВАЖНО: вернуть не Матч, а Матчснапшот (ибо матч - мутейбл, сов семи вытекающими)

    public Match addPoint(String name, UUID uuid) {
        Match match = getMatch(uuid);

        synchronized (match) {
            if (!match.isFinished() && getMatch(uuid).equals(match)) {
                name = name.trim().toLowerCase();
                Side side = null;
                if (match.getPlayer1().getName().equals(name)) {
                    side = Side.A;
                } else if (match.getPlayer2().getName().equals(name)) {
                    side = Side.B;
                } else {
                    //400 - сторона не найдена
                }

                match.getMatchScore().increasePoint(side);
                if (match.isFinished()) {
                    //постоянка - save
                    ongoingMatchesStorage.remove(uuid, match);
                }
            } else {
                //матч уже завершен (т.е. странность)
            }
        }

        return match;
    }

    public List<Match> getFinishedMatches(int page, String playerName) { //optionalFields!
        //сервис должен проверить (валидация) и селать логику по полям выше
        // с неймом - кк выше
        // с пейджем...
        // еесли пейджа нет - показать все страницы (берем 5 матчей на страницу, например)
        // если есть - показать конкретную страницу с матчами

        //сервис:
        //если playerName == null - вообще все завершенне матчи
        // если не нуль - завершенные - где есть иммя (А или Б)
        return List.of();
    }

}
