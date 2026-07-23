package tennisboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.repository.MatchRepository;
import tennisboard.repository.PlayerRepository;
import tennisboard.service.logic.Match;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FinishedMatchService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public void saveMatch(Match match, UUID uuid) {
        String name1 = match.getPlayer1().getName();
        Optional<PlayerEntity> optionalPlayer1Entity = playerRepository.findByName(name1);
        PlayerEntity player1;
        if (optionalPlayer1Entity.isPresent()) {
            player1 = optionalPlayer1Entity.get();
        } else {
            player1 = playerRepository.save(new PlayerEntity(name1));
        }

        String name2 = match.getPlayer2().getName();
        Optional<PlayerEntity> optionalPlayer2Entity = playerRepository.findByName(name2);
        PlayerEntity player2;
        if (optionalPlayer2Entity.isPresent()) {
            player2 = optionalPlayer2Entity.get();
        } else {
            player2 = playerRepository.save(new PlayerEntity(name2));
        }

        PlayerEntity winner = match.getWinner().getName().equals(name1) ? player1 : player2;

        MatchEntity match1 = new MatchEntity(player1, player2, winner);
        matchRepository.save(match1);
    }
}
