package tennisboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchService {
    private final static int MIN_PAGE = 1;
    private static final int PAGE_ELEMENTS_SIZE = 5;

    private final MatchInternalMapper internalMapper;
    private final OngoingMatchesStorage ongoingMatchesStorage;
    private final MatchRepository matchRepository;
    private final FinishedMatchService finishedMatchService;

    public UUID createNewMatch(String firstPlayerName, String secondPlayerName) {
        validatePlayerName(firstPlayerName);
        validatePlayerName(secondPlayerName);

        String normalizedFirstPlayerName = firstPlayerName.trim().toLowerCase();
        String normalizedSecondPlayerName = secondPlayerName.trim().toLowerCase();
        validatePlayersNames(normalizedFirstPlayerName, normalizedSecondPlayerName);

        UUID id = UUID.randomUUID();
        Match match = new Match(
                id,
                new Player(null, normalizedFirstPlayerName),
                new Player(null, normalizedSecondPlayerName),
                new MatchScore()
        );

        ongoingMatchesStorage.save(match);
        log.info("Match is created and  saved into memory: UUID={}, player1Name={}, player2Name={}",
                id,
                normalizedFirstPlayerName,
                normalizedSecondPlayerName);

        return id;
    }

    private void validatePlayersNames(String normalizedFirstPlayerName, String normalizedSecondPlayerName) {
        if (normalizedFirstPlayerName.equals(normalizedSecondPlayerName)) {
            throw new MatchValidationException(
                    "Names are the same!"
            );
        }
    }

    public MatchSnapshot getMatchSnapshot(UUID uuid) {
        Match match = getMatch(uuid);

        synchronized (match) {
            return internalMapper.toMatchSnapshot(match);
        }
    }

    /**
     * Найти match по uuid — нормально без synchronized(match)
     * Но. Остальное лочить необходимо, т.к. между моментом нахождения и изменения одним потоком,
     * матч может изменить другой.
     *
     * @param name - имя игрока
     * @param uuid - айди матча
     * @return снапшот матча
     */

    public MatchSnapshot addPoint(String name, UUID uuid) {
        Match match = getMatch(uuid);

        synchronized (match) {
            if (!match.isFinished()) {
                validatePlayerName(name);

                name = name.trim().toLowerCase();
                Side side = getSide(name, match);

                match.getMatchScore().increasePoint(side);
                if (match.isFinished()) {
                    MatchSnapshot snapshot = internalMapper.toMatchSnapshot(match);
                    finishedMatchService.saveMatch(match, uuid);
                    ongoingMatchesStorage.remove(uuid, match);
                    log.debug("Match is finished and removed, has id: UUID={}, player1Name={}, player2Name={}, winner={}",
                            match.getMatchId(),
                            match.getPlayer1().getName(),
                            match.getPlayer2().getName(),
                            match.getWinner().getName()
                    );
                    return snapshot;
                }
            } else {
                throw new MatchAlreadyFinishedException(
                        "Match is found, yet is already finished!"
                );
            }

            log.debug("Ongoing match has id: UUID={}, player1Name={}, player2Name={}",
                    match.getMatchId(),
                    match.getPlayer1().getName(),
                    match.getPlayer2().getName()
            );
            return internalMapper.toMatchSnapshot(match);
        }
    }

    private Side getSide(String name, Match match) {
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
        return side;
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

    /**
     * Получает страницу завершённых матчей с участием указанного игрока
     *
     * @param page       (должен быть более или равным 1)
     * @param playerName (не должен быть null или пустым)
     * @return FinishedMatchesEssentialInfoDTO - состоит из замаппанного shortMatchInfoDTOList
     * с именами игроков и победителем, page для показа конкретной страницы и totalPagesForPlayer
     * - сколько всего страниц есть у данного игрока в БД
     */

    public FinishedMatchesEssentialInfoDTO getFinishedMatches(int page, String playerName) {
        validatePageNumber(page);

        int offset = (page - 1) * PAGE_ELEMENTS_SIZE;

        List<MatchEntity> filteredMatches;
        long totalMatches;

        if (StringUtils.hasText(playerName)) {
            playerName = playerName.toLowerCase().trim();
            filteredMatches =
                    matchRepository.findAllMatchesByPlayerNameFiltered(offset, PAGE_ELEMENTS_SIZE, playerName);
            totalMatches = matchRepository.countMatchesPlayedByPlayer(playerName);
        } else {
            filteredMatches =
                    matchRepository.findAllMatchesFiltered(offset, PAGE_ELEMENTS_SIZE);
            totalMatches = matchRepository.countAllMatches();
        }

        List<ShortMatchInfoDTO> shortMatchInfoDTOList
                = internalMapper.toShortMatchInfoDTOList(filteredMatches);

        int totalPages = countTotalPages(totalMatches);
        log.info("Found finished matches: totalMatches={}, page={}, totalPages={}, playerName={}",
                totalMatches,
                page,
                totalPages,
                playerName
        );
        return new FinishedMatchesEssentialInfoDTO(
                shortMatchInfoDTOList,
                page,
                totalPages);
    }

    private int countTotalPages(long totalMatches) {
        return (int) Math.ceil((double) totalMatches
                / PAGE_ELEMENTS_SIZE);
    }

    private void validatePageNumber(int page) {
        if (page < MIN_PAGE) {
            throw new MatchValidationException(String.format(
                    "Page number %d should be more or equal to %d", page, MIN_PAGE
            ));
        }
    }

    private void validatePlayerName(String playerName) {
        if (!StringUtils.hasText(playerName)) {
            throw new MatchValidationException(String.format(
                    "Name %s cannot be null or empty", playerName
            ));
        }
    }
}