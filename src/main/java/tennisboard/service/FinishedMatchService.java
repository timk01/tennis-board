package tennisboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.repository.MatchRepository;
import tennisboard.repository.PlayerRepository;
import tennisboard.service.logic.Match;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FinishedMatchService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public void saveMatch(Match match) {
        String firstPlayerName = match.getPlayer1().getName();
        PlayerEntity firstPlayerEntity = getPlayerEntity(firstPlayerName);

        String secondPlayerName = match.getPlayer2().getName();
        PlayerEntity secondPlayerEntity = getPlayerEntity(secondPlayerName);

        PlayerEntity winner = getWinner(match, firstPlayerName, firstPlayerEntity, secondPlayerEntity);

        MatchEntity matchEntity = new MatchEntity(firstPlayerEntity, secondPlayerEntity, winner);
        matchRepository.save(matchEntity);

        log.info("Finished match is saved into matchRepository: UUID={}, firstPlayerEntity={}, secondPlayerEntity={}, winner={}",
                match.getMatchId(),
                firstPlayerEntity,
                secondPlayerEntity,
                winner);
    }

    private PlayerEntity getPlayerEntity(String name) {
        Optional<PlayerEntity> optionalPlayerEntity = playerRepository.findByName(name);
        return optionalPlayerEntity.orElseGet(() -> playerRepository.save(new PlayerEntity(name)));
    }


    private PlayerEntity getWinner(
            Match match,
            String firstPlayerName,
            PlayerEntity firstPlayerEntity,
            PlayerEntity secondPlayerEntity
    ) {
        return match.getWinner().getName().equals(firstPlayerName)
                ? firstPlayerEntity
                : secondPlayerEntity;
    }
}