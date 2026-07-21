package tennisboard.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tennisboard.dto.FinishedMatchesEssentialInfoDTO;
import tennisboard.dto.MatchSnapshot;
import tennisboard.dto.ShortMatchInfoDTO;
import tennisboard.entity.MatchEntity;
import tennisboard.exception.MatchAlreadyFinishedException;
import tennisboard.exception.MatchIsNotFoundException;
import tennisboard.exception.MatchValidationException;
import tennisboard.exception.SideIsNotFoundException;
import tennisboard.mapper.MatchInternalMapper;
import tennisboard.repository.MatchRepository;
import tennisboard.repository.PlayerRepository;
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
    private final static int MIN_PAGE = 1;
    private static final int PAGE_ELEMENTS_SIZE = 5;

    private final MatchInternalMapper internalMapper;
    private final OngoingMatchesStorage ongoingMatchesStorage;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final FinishedMatchService finishedMatchService;

    public MatchService(
            MatchInternalMapper internalMapper,
            OngoingMatchesStorage ongoingMatchesStorage,
            PlayerRepository playerRepository,
            MatchRepository matchRepository,
            FinishedMatchService finishedMatchService
    ) {
        this.internalMapper = internalMapper;
        this.ongoingMatchesStorage = ongoingMatchesStorage;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.finishedMatchService = finishedMatchService;
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
                Side side;
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
                    MatchSnapshot snapshot = internalMapper.toMatchSnapshot(match);
                    finishedMatchService.saveMatch(match, uuid);
                    ongoingMatchesStorage.remove(uuid, match);
                    return snapshot;
                }
            } else {
                throw new MatchAlreadyFinishedException(
                        "Match is found, yet is already finished!"
                );
            }
            return internalMapper.toMatchSnapshot(match);
        }
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

    public FinishedMatchesEssentialInfoDTO getFinishedMatches(int page, String playerName) {
        /*
        если судья говорит "покажи 5 страницу"
        допустим у нас размер страницы (количество показываемых элементов) = 5 (минпейдж выше)
        limit = 5 (PAGE_ELEMENTS_SIZE)
        1 страница: 1-2-3-4-5, вторая 6-7-8-9-10, третья 11-12-13-14-15
        т.е. будет:
        select *
        from matches m
        order by m.id
        limit 5 offset 10;
        пропусти первые 10 строк (оффсет, скип), ограничь показ первыми 5
        offset = (page - 1) * limit;
        ((если страницы page, он просто покажет первые 5 энтитей))
         */

        if (page < MIN_PAGE) {
            throw new MatchValidationException(String.format(
                    "Page number %d should be more or equal to %d", page, MIN_PAGE
            ));
        }

        if (StringUtils.hasText(playerName)) {
            playerName = playerName.toLowerCase().trim();
        }

        List<MatchEntity> matchEntities = matchRepository.findAll();

        long offset = (long) (page - 1) * PAGE_ELEMENTS_SIZE;
        //toDo фильтр должен быть на уровне БД, т.е. я в файндОлл передаю:
        //плеер, пейдж, сайз (который сейас 5)
        List<MatchEntity> filteredEntities = matchEntities.stream()
                .skip(offset)
                .limit(PAGE_ELEMENTS_SIZE)
                .toList();

        List<ShortMatchInfoDTO> shortMatchInfoDTOList
                = internalMapper.toShortMatchInfoDTOList(filteredEntities);

        int totalPages = matchEntities.size() / PAGE_ELEMENTS_SIZE;

        return new FinishedMatchesEssentialInfoDTO(
                shortMatchInfoDTOList,
                page,
                PAGE_ELEMENTS_SIZE);
    }
}