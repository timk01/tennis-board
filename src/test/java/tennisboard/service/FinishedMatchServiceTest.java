package tennisboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.repository.MatchRepository;
import tennisboard.repository.PlayerRepository;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinishedMatchServiceTest {

    @InjectMocks
    FinishedMatchService finishedMatchService;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private MatchRepository matchRepository;

    @Captor
    private ArgumentCaptor<MatchEntity> matchEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<PlayerEntity> playerEntityArgumentCaptor;

    @Test
    void matchIsSavedWhenBothPlayersArefFound() {
        String player1Name = "federerr";
        Optional<PlayerEntity> playerEntity1 = Optional.of(new PlayerEntity(
                player1Name
        ));
        when(playerRepository.findByName(player1Name)).thenReturn(playerEntity1);

        String player2Name = "agassi";
        Optional<PlayerEntity> playerEntity2 = Optional.of(new PlayerEntity(
                player2Name
        ));
        when(playerRepository.findByName(player2Name)).thenReturn(playerEntity2);

        Match match = preparedFinishedMatchWithWinnerA(player1Name, player2Name);
        finishedMatchService.saveMatch(match, match.getMatchId());

        verify(playerRepository, times(1)).findByName(player1Name);
        verify(playerRepository, times(1)).findByName(player2Name);
        verify(playerRepository, never()).save(any(PlayerEntity.class));

        verify(matchRepository, times(1)).save(matchEntityArgumentCaptor.capture());

        MatchEntity capturedMatchEntity = matchEntityArgumentCaptor.getValue();

        assertThat(capturedMatchEntity).isNotNull();
        assertThat(capturedMatchEntity.getFirstPlayer().getName()).isEqualTo(player1Name);
        assertThat(capturedMatchEntity.getSecondPlayer().getName()).isEqualTo(player2Name);
        assertThat(capturedMatchEntity.getWinner().getName()).isEqualTo(player1Name);
    }

    @Test
    void matchIsSavedWhenOnePlayerIsNotFoundWhileSecondIsFound() {
        String player1Name = "federerr";
        when(playerRepository.findByName(player1Name)).thenReturn(Optional.empty());
        Optional<PlayerEntity> playerEntity1 = Optional.of(new PlayerEntity(
                player1Name
        ));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity1.get());

        String player2Name = "agassi";
        Optional<PlayerEntity> playerEntity2 = Optional.of(new PlayerEntity(
                player2Name
        ));
        when(playerRepository.findByName(player2Name)).thenReturn(playerEntity2);

        Match match = preparedFinishedMatchWithWinnerA(player1Name, player2Name);
        finishedMatchService.saveMatch(match, match.getMatchId());

        verify(playerRepository, times(1)).findByName(player1Name);
        verify(playerRepository, times(1)).findByName(player2Name);

        verify(playerRepository, times(1)).save(playerEntityArgumentCaptor.capture());
        PlayerEntity capturedPlayerEntity = playerEntityArgumentCaptor.getValue();
        assertThat(capturedPlayerEntity.getName()).isEqualTo(player1Name);

        verify(matchRepository, times(1)).save(matchEntityArgumentCaptor.capture());
        MatchEntity capturedMatchEntity = matchEntityArgumentCaptor.getValue();

        assertThat(capturedMatchEntity).isNotNull();
        assertThat(capturedMatchEntity.getFirstPlayer().getName()).isEqualTo(player1Name);
        assertThat(capturedMatchEntity.getSecondPlayer().getName()).isEqualTo(player2Name);
        assertThat(capturedMatchEntity.getWinner().getName()).isEqualTo(player1Name);
    }

    @Test
    void matchIsSavedWhenOnePlayerIsFoundWhileSecondIsNotFound() {
        String player1Name = "federerr";
        Optional<PlayerEntity> playerEntity1 = Optional.of(new PlayerEntity(
                player1Name
        ));
        when(playerRepository.findByName(player1Name)).thenReturn(playerEntity1);

        String player2Name = "agassi";
        when(playerRepository.findByName(player2Name)).thenReturn(Optional.empty());
        Optional<PlayerEntity> playerEntity2 = Optional.of(new PlayerEntity(
                player2Name
        ));
        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(playerEntity2.get());

        Match match = preparedFinishedMatchWithWinnerB(player1Name, player2Name);
        finishedMatchService.saveMatch(match, match.getMatchId());

        verify(playerRepository, times(1)).findByName(player1Name);
        verify(playerRepository, times(1)).findByName(player2Name);

        verify(playerRepository, times(1)).save(playerEntityArgumentCaptor.capture());
        PlayerEntity capturedPlayerEntity = playerEntityArgumentCaptor.getValue();
        assertThat(capturedPlayerEntity.getName()).isEqualTo(player2Name);

        verify(matchRepository, times(1)).save(matchEntityArgumentCaptor.capture());
        MatchEntity capturedMatchEntity = matchEntityArgumentCaptor.getValue();

        assertThat(capturedMatchEntity).isNotNull();
        assertThat(capturedMatchEntity.getFirstPlayer().getName()).isEqualTo(player1Name);
        assertThat(capturedMatchEntity.getSecondPlayer().getName()).isEqualTo(player2Name);
        assertThat(capturedMatchEntity.getWinner().getName()).isEqualTo(player2Name);
    }

    private Match preparedMatch(String playerName1, String playerName2) {
        UUID id = UUID.randomUUID();
        return new Match(
                id,
                new Player(null, playerName1),
                new Player(null, playerName2),
                new MatchScore()
        );
    }

    private Match preparedFinishedMatchWithWinnerA(String player1Name, String player2Name) {
        Match match = preparedMatch(player1Name, player2Name);

        for (int i = 0; i < 48; i++) {
            match.getMatchScore().increasePoint(Side.A);
        }

        return match;
    }

    private Match preparedFinishedMatchWithWinnerB(String player1Name, String player2Name) {
        Match match = preparedMatch(player1Name, player2Name);

        for (int i = 0; i < 48; i++) {
            match.getMatchScore().increasePoint(Side.B);
        }

        return match;
    }
}

    