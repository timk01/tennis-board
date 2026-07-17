package tennisboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tennisboard.exception.MatchIsNotFoundException;
import tennisboard.exception.MatchValidationException;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.storage.OngoingMatches;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class MatchServiceTest {
    private OngoingMatchesStorage storage;
    private MatchService service;

    @BeforeEach
    void init() {
        storage = new OngoingMatches();
        service = new MatchService(storage);
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

    @Test
    void getMatchSuccess() {
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

        Match resultMatch = service.getMatch(id);

        assertThat(resultMatch).isNotNull();
        assertThat(resultMatch).isEqualTo(match);
    }

    @Test
    void getMatchFailsDueToNullId() {
        assertThatThrownBy(() -> service.getMatch(null))
                .hasMessage("ID cannot be null")
                .isInstanceOf(MatchValidationException.class);
    }

    @Test
    void getMatchFailsDueToWrongId() {
        assertThatThrownBy(() -> service.getMatch(UUID.fromString("91f1b06b-aa1a-482d-95f2-cf52c968f3f0")))
                .isInstanceOf(MatchIsNotFoundException.class);
    }
}