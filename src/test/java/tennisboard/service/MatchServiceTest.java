package tennisboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tennisboard.dto.FinishedMatchesEssentialInfoDTO;
import tennisboard.dto.MatchSnapshot;
import tennisboard.dto.ShortMatchInfoDTO;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.exception.MatchIsNotFoundException;
import tennisboard.exception.MatchValidationException;
import tennisboard.exception.SideIsNotFoundException;
import tennisboard.mapper.MatchInternalMapper;
import tennisboard.repository.MatchRepository;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;
import tennisboard.storage.OngoingMatches;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {
    @Mock
    private MatchInternalMapper internalMapper;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private FinishedMatchService finishedMatchService;

    private OngoingMatchesStorage storage;
    private MatchService service;

    @BeforeEach
    void init() {
        storage = new OngoingMatches();
        service = new MatchService(internalMapper, storage, matchRepository, finishedMatchService);
    }

    @Test
    void createNewMatchSuccess() {
        String firstName = "federer";
        String secondName = "agassi";

        UUID resultMatchId = service.createNewMatch(firstName, secondName);

        assertThat(resultMatchId).isNotNull();
        assertThat(storage.findById(resultMatchId)).isPresent();

        Match match = storage.findById(resultMatchId).get();
        assertThat(resultMatchId).isEqualTo(match.getMatchId());

        assertThat(match.getPlayer1().getName()).isEqualTo(firstName);
        assertThat(match.getPlayer2().getName()).isEqualTo(secondName);
    }

    @Test
    void createNewMatchFailsSinceFirstNameIsNull() {
        String firstName = null;
        String secondName = "abc";

        assertThatThrownBy(() -> service.createNewMatch(firstName, secondName))
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void createNewMatchFailsSinceSecondNameIsEmpty() {
        String firstName = "abc";
        String secondName = "";

        assertThatThrownBy(() -> service.createNewMatch(firstName, secondName))
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void createNewMatchFailsSinceFirstNameIsNullAndSecondNameIsEmpty() {
        String firstName = null;
        String secondName = "";

        assertThatThrownBy(() -> service.createNewMatch(firstName, secondName))
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void createNewMatchFailDueTToTheSameNames() {
        String firstName = "Federer";
        String secondName = "federer";

        assertThatThrownBy(() -> service.createNewMatch(firstName, secondName))
                .hasMessage("Names are the same!")
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void createNewMatchFailDueTToTheSameNamesAndOneHaveGaps() {
        String firstName = "Federer";
        String secondName = " federer ";

        assertThatThrownBy(() -> service.createNewMatch(firstName, secondName))
                .hasMessage("Names are the same!")
                .isInstanceOf(MatchValidationException.class);
    }

    /**
     * Здесь тестируется реальный MatchService.
     * <p>
     * Storage реальная, поэтому getMatchSnapshot(id) сможет найти Match по id - без проблем
     * ЕЕ МОКАТЬ НЕ НАДО! (времянка - вполне себе реальна)
     * Но internalMapper — mock, поэтому его метод toMatchSnapshot(...) сам по себе
     * ничего не сконвертирует и по умолчанию вернёт null (тупо заглушка)
     * <p>
     * Поэтому заранее описываем поведение mock-а:
     * когда internalMapper.toMatchSnapshot(match) будет вызван с этим Match (дойдет до него)
     * вернуть заранее подготовленный expectedSnapshot.
     * Который (соверщенно внезапно) внучную переложен из значимых полей
     */
    @Test
    void getMatchSuccess() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        MatchSnapshot expectedSnapshot = newEmptySnapshot(result.firstName, result.secondName);
        when(internalMapper.toMatchSnapshot(result.match)).thenReturn(expectedSnapshot);

        MatchSnapshot actualSnapshot = service.getMatchSnapshot(result.id);

        assertThat(actualSnapshot).isEqualTo(expectedSnapshot);
    }

    @Test
    void getMatchFailsDueToNullId() {
        assertThatThrownBy(() -> service.getMatchSnapshot(null))
                .hasMessage("ID cannot be null")
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void getMatchFailsDueToWrongId() {
        assertThatThrownBy(() -> service.getMatchSnapshot(UUID.fromString("91f1b06b-aa1a-482d-95f2-cf52c968f3f0")))
                .isInstanceOf(MatchIsNotFoundException.class);
    }

    @Test
    void addPointSuccessForASide() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        MatchSnapshot expectedSnapshot = snapShotWithChangedPoints(
                result.firstName(),
                result.secondName(),
                "15",
                "0");
        when(internalMapper.toMatchSnapshot(result.match())).thenReturn(expectedSnapshot);

        MatchSnapshot actualSnapshot = service.addPoint("Federer", result.id());

        assertThat(result.match().getMatchScore().getPointA()).isEqualTo(15);
        assertThat(actualSnapshot).isEqualTo(expectedSnapshot);
    }

    @Test
    void addPointSuccessForBSide() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        MatchSnapshot expectedSnapshot = snapShotWithChangedPoints(
                result.firstName(),
                result.secondName(),
                "0",
                "15");
        when(internalMapper.toMatchSnapshot(result.match())).thenReturn(expectedSnapshot);

        MatchSnapshot actualSnapshot = service.addPoint("Agassi", result.id());

        assertThat(result.match().getMatchScore().getPointB()).isEqualTo(15);
        assertThat(actualSnapshot).isEqualTo(expectedSnapshot);
    }

    @Test
    void addMatchFailsDueToNullId() {
        assertThatThrownBy(() -> service.addPoint("abc", null))
                .hasMessage("ID cannot be null")
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void addMatchFailsDueToWrongId() {
        assertThatThrownBy(() -> service.addPoint(
                "abc",
                UUID.fromString("91f1b06b-aa1a-482d-95f2-cf52c968f3f0")
        ))
                .isInstanceOf(MatchIsNotFoundException.class);
    }

    @Test
    void addMatchFailsDueToNullName() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        assertThatThrownBy(() -> service.addPoint(
                null,
                result.id
        ))
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void addMatchFailsDueToNotFoundName() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        assertThatThrownBy(() -> service.addPoint(
                "abc",
                result.id
        ))
                .isInstanceOf(SideIsNotFoundException.class);
    }

    @Test
    void addLastPointForASideShouldSaveAndRemoveMatchWhenPointFinishesMatch() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        MatchSnapshot expectedSnapshot = snapShotWithWinner(
                result.firstName(),
                result.secondName(),
                "0",
                "0",
                "federer");
        when(internalMapper.toMatchSnapshot(result.match())).thenReturn(expectedSnapshot);

        MatchSnapshot actualSnapshot = null;
        for (int i = 0; i < 48; i++) {
            actualSnapshot = service.addPoint("Federer", result.id());
        }

        assertThat(result.match().getMatchScore().isMatchFinished()).isTrue();
        assertThat(result.match().getMatchScore().getWinner()).isEqualTo(Side.A);
        verify(finishedMatchService, times(1)).saveMatch(result.match(), result.id());
        assertThat(storage.findById(result.id()).isEmpty()).isTrue();
        assertThat(actualSnapshot).isEqualTo(expectedSnapshot);
    }

    @Test
    void addLastPointForBSideShouldSaveAndRemoveMatchWhenPointFinishesMatch() {
        SavedEmptyMatch result = getSavedEmptyMatch();

        MatchSnapshot expectedSnapshot = snapShotWithWinner(
                result.firstName(),
                result.secondName(),
                "0",
                "0",
                "agassi");
        when(internalMapper.toMatchSnapshot(result.match())).thenReturn(expectedSnapshot);

        MatchSnapshot actualSnapshot = null;
        for (int i = 0; i < 48; i++) {
            actualSnapshot = service.addPoint("Agassi", result.id());
        }

        assertThat(result.match().getMatchScore().isMatchFinished()).isTrue();
        assertThat(result.match().getMatchScore().getWinner()).isEqualTo(Side.B);
        verify(finishedMatchService, times(1)).saveMatch(result.match(), result.id());
        assertThat(storage.findById(result.id()).isEmpty()).isTrue();
        assertThat(actualSnapshot).isEqualTo(expectedSnapshot);
    }

    @Test
    void getFinishedMatchesFromTwoMatchesWithDifferentWinnersAndZeroOffsetReturnsProperResult() {
        String playerName = "federer";
        String opponentName = "agassi";

        List<MatchEntity> matches = new ArrayList<>(List.of(
                new MatchEntity(
                        new PlayerEntity(playerName),
                        new PlayerEntity(opponentName),
                        new PlayerEntity(playerName)
                ),
                new MatchEntity(
                        new PlayerEntity(playerName),
                        new PlayerEntity(opponentName),
                        new PlayerEntity(opponentName)
                )
        ));
        when(matchRepository.findAllMatchesByPlayerNameFiltered(0, 5, playerName))
                .thenReturn(matches);

        List<ShortMatchInfoDTO> convertedShortMatchesDto = new ArrayList<>(List.of(
                new ShortMatchInfoDTO(
                        playerName,
                        opponentName,
                        playerName
                ),
                new ShortMatchInfoDTO(
                        playerName,
                        opponentName,
                        opponentName
                )
        ));
        when(internalMapper.toShortMatchInfoDTOList(matches)).thenReturn(convertedShortMatchesDto);

        int playerMatches = 2;
        when(matchRepository.countMatchesPlayedByPlayer(playerName))
                .thenReturn((long) playerMatches);

        int page = 1;
        FinishedMatchesEssentialInfoDTO expected = new FinishedMatchesEssentialInfoDTO(
                convertedShortMatchesDto,
                page,
                1);

        FinishedMatchesEssentialInfoDTO result = service.getFinishedMatches(1, playerName);

        assertThat(result).isEqualTo(expected);

        verify(matchRepository, times(1))
                .findAllMatchesByPlayerNameFiltered(0, 5, playerName);
        verify(internalMapper, times(1)).toShortMatchInfoDTOList(matches);
        verify(matchRepository, times(1))
                .countMatchesPlayedByPlayer(playerName);
    }

    @Test
    void getFinishedMatchesFromZeroMatchesReturnsEmptyRecord() {
        String playerName = "federer";

        List<MatchEntity> emptyMatches = new ArrayList<>(List.of(
        ));

        when(matchRepository.findAllMatchesByPlayerNameFiltered(0, 5, playerName))
                .thenReturn(emptyMatches);

        List<ShortMatchInfoDTO> convertedShortMatchesDto = new ArrayList<>(List.of(
        ));
        when(internalMapper.toShortMatchInfoDTOList(emptyMatches)).thenReturn(convertedShortMatchesDto);

        int playerMatches = 0;
        when(matchRepository.countMatchesPlayedByPlayer(playerName))
                .thenReturn((long) playerMatches);

        int page = 1;
        FinishedMatchesEssentialInfoDTO expected = new FinishedMatchesEssentialInfoDTO(
                convertedShortMatchesDto,
                page,
                0);

        FinishedMatchesEssentialInfoDTO result = service.getFinishedMatches(1, playerName);

        assertThat(result).isEqualTo(expected);

        verify(matchRepository, times(1))
                .findAllMatchesByPlayerNameFiltered(0, 5, playerName);
        verify(internalMapper, times(1)).toShortMatchInfoDTOList(emptyMatches);
        verify(matchRepository, times(1))
                .countMatchesPlayedByPlayer(playerName);
    }


    @Test
    void getFinishedMatchesFromTwoMatchesWithoutNamesAndWithZeroOffsetReturnsProperResult() {
        String playerName = "federer";
        String opponentName = "agassi";

        List<MatchEntity> matches = new ArrayList<>(List.of(
                new MatchEntity(
                        new PlayerEntity(playerName),
                        new PlayerEntity(opponentName),
                        new PlayerEntity(playerName)
                ),
                new MatchEntity(
                        new PlayerEntity(playerName),
                        new PlayerEntity(opponentName),
                        new PlayerEntity(opponentName)
                )
        ));
        when(matchRepository.findAllMatchesFiltered(0, 5))
                .thenReturn(matches);

        List<ShortMatchInfoDTO> convertedShortMatchesDto = new ArrayList<>(List.of(
                new ShortMatchInfoDTO(
                        playerName,
                        opponentName,
                        playerName
                ),
                new ShortMatchInfoDTO(
                        playerName,
                        opponentName,
                        opponentName
                )
        ));
        when(internalMapper.toShortMatchInfoDTOList(matches)).thenReturn(convertedShortMatchesDto);

        int playerMatches = 2;
        when(matchRepository.countAllMatches())
                .thenReturn((long) playerMatches);

        int page = 1;
        FinishedMatchesEssentialInfoDTO expected = new FinishedMatchesEssentialInfoDTO(
                convertedShortMatchesDto,
                page,
                1);

        FinishedMatchesEssentialInfoDTO result = service.getFinishedMatches(1, null);

        assertThat(result).isEqualTo(expected);

        verify(matchRepository, times(1))
                .findAllMatchesFiltered(0, 5);
        verify(internalMapper, times(1)).toShortMatchInfoDTOList(matches);
        verify(matchRepository, times(1))
                .countAllMatches();
    }

    @Test
    void getFinishedMatchesFailsDueToZeroReceivedPages() {
        assertThatThrownBy(() -> service.getFinishedMatches(0, "federer"))
                .isInstanceOf(MatchValidationException.class);
    }

    private MatchSnapshot newEmptySnapshot(String firstName, String secondName) {
        return new MatchSnapshot(
                firstName,
                secondName,
                "0",
                "0",
                0,
                0,
                0,
                0,
                null,
                null,
                null
        );
    }

    private MatchSnapshot snapShotWithWinner(
            String firstName,
            String secondName,
            String firstPlayerPoints,
            String secondPlayerPoints,
            String winnerName
    ) {
        return new MatchSnapshot(
                firstName,
                secondName,
                firstPlayerPoints,
                secondPlayerPoints,
                0,
                0,
                0,
                0,
                null,
                null,
                winnerName
        );
    }

    private MatchSnapshot snapShotWithChangedPoints(
            String firstName,
            String secondName,
            String firstPlayerPoints,
            String secondPlayerPoints
    ) {
        return new MatchSnapshot(
                firstName,
                secondName,
                firstPlayerPoints,
                secondPlayerPoints,
                0,
                0,
                0,
                0,
                null,
                null,
                null
        );
    }

    private SavedEmptyMatch getSavedEmptyMatch() {
        String firstName = "Federer";
        String secondName = "Agassi";

        UUID id = UUID.randomUUID();
        Match match = new Match(
                id,
                new Player(null, firstName.toLowerCase()),
                new Player(null, secondName.toLowerCase()),
                new MatchScore()
        );

        storage.save(match);
        SavedEmptyMatch result = new SavedEmptyMatch(firstName, secondName, id, match);
        return result;
    }

    private record SavedEmptyMatch(String firstName, String secondName, UUID id, Match match) {

    }
}
