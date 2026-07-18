package tennisboard.service;

import org.springframework.stereotype.Service;
import tennisboard.dto.MatchSnapshot;
import tennisboard.exception.MatchIsNotFoundException;
import tennisboard.exception.MatchValidationException;
import tennisboard.exception.SideIsNotFoundException;
import tennisboard.exception.MatchAlreadyFinishedException;
import tennisboard.mapper.MatchInternalMapper;
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
    private final MatchInternalMapper internalMapper;
    private final OngoingMatchesStorage ongoingMatchesStorage;

    public MatchService(MatchInternalMapper internalMapper, OngoingMatchesStorage ongoingMatchesStorage) {
        this.internalMapper = internalMapper;
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

    public MatchSnapshot getMatchSnapshot(UUID uuid) {
        return internalMapper.toMatchSnapshot(getMatch(uuid));
    }


    //todo ЗДЕСЬ ВАЖНО: вернуть не Матч, а Матчснапшот (ибо матч - мутейбл, сов семи вытекающими)

    /**
     * Найти match по uuid — нормально без synchronized(match)
     * Но. Остальное лочить необходимо, т.к. между моментом нахождения и изменения одним потоком,
     * матч может изменить другой.
     *
     * @param name - имя игрока
     * @param uuid - айди матча
     * @return
     */

    public MatchSnapshot addPoint(String name, UUID uuid) {
        Match match = getMatch(uuid);

        synchronized (match) {
            if (!match.isFinished()) {
                if (name == null || name.isBlank()) {
                    throw new MatchValidationException(String.format(
                            "Name %s cannot be null or empty", name
                    ));
                }

                name = name.trim().toLowerCase();
                Side side = null;
                if (match.getPlayer1().getName().equals(name)) {
                    side = Side.A;
                } else if (match.getPlayer2().getName().equals(name)) {
                    side = Side.B;
                } else {
                    throw new SideIsNotFoundException(
                            "Match is found, but cannot find the passed player side in it"
                    );
                }

                match.getMatchScore().increasePoint(side);
                if (match.isFinished()) {
                    //постоянка - save
                    ongoingMatchesStorage.remove(uuid, match);
                }
            } else {
                throw new MatchAlreadyFinishedException(
                        "Match is found, yet is already finished!"
                );
            }
        }

        return internalMapper.toMatchSnapshot(getMatch(uuid));
    }

    private Match getMatch(UUID uuid) {
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
