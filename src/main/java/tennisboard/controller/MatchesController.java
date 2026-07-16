package tennisboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tennisboard.request.CreateMatchRequest;
import tennisboard.response.CreateMatchResponse;
import tennisboard.response.MatchScoreResponse;
import tennisboard.service.MatchService;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.StatusOfGame;
import tennisboard.service.logic.StatusOfSet;

import java.util.UUID;

@RestController
@RequestMapping("/matches")
public class MatchesController {
    private final MatchService matchService;

    public MatchesController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<CreateMatchResponse> createNewMatch(@RequestBody CreateMatchRequest request) {
        String name1 = request.firstPlayerName();
        String name2 = request.secondPlayerName();
        UUID matchId = matchService.createNewMatch(name1, name2);

        return new ResponseEntity<>(new CreateMatchResponse(matchId),  HttpStatus.CREATED);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<MatchScoreResponse> getMatchStats(@PathVariable("uuid") UUID uuid) {
        Match match = matchService.getMatch(uuid);
        MatchScoreResponse response = fillCurrentMatchStat(match);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    private MatchScoreResponse fillCurrentMatchStat(Match match) {
        StatusOfSet currentStatusOfSet = match.getMatchScore().getStatusOfSet();
        StatusOfGame currentStatusOfGame = match.getMatchScore().getStatusOfGame();

        String firstPlayerPoints;
        String secondPlayerPoints;

        if (currentStatusOfSet == StatusOfSet.TIE_BREAK) {
            firstPlayerPoints = null;
            secondPlayerPoints = null;
        } else if (currentStatusOfGame == StatusOfGame.ADVANTAGE_A) {
            firstPlayerPoints = "AD";
            secondPlayerPoints = "40";
        } else if (currentStatusOfGame == StatusOfGame.ADVANTAGE_B) {
            firstPlayerPoints = "40";
            secondPlayerPoints = "AD";
        } else {
            firstPlayerPoints = String.valueOf(match.getMatchScore().getPointA());
            secondPlayerPoints = String.valueOf(match.getMatchScore().getPointB());
        }

        String winnerName = match.isFinished() ? match.getWinner().getName() : null;

        return new MatchScoreResponse(
                match.getPlayer1().getName(),
                match.getPlayer2().getName(),
                firstPlayerPoints,
                secondPlayerPoints,
                match.getMatchScore().getGameA(),
                match.getMatchScore().getGameB(),
                match.getMatchScore().getSetA(),
                match.getMatchScore().getSetB(),
                match.getMatchScore().getTieBreakPointA(),
                match.getMatchScore().getTieBreakPointB(),
                winnerName
        );
    }

/*    @PostMapping("/{uuid}/point")
    public ResponseEntity<BigUglyStat> addPoint(@RequestBody Something playerName) { //something = record with fields, as i think
        return new ResponseEntity<>(BigUglyStat, HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BigUglyStat> getMatchStats(@PathVariable UUID uuid) {
        return new ResponseEntity<>(BigUglyStat, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<finishedMatchesList> getFinishedMatches() {
        return new ResponseEntity<finishedMatchesList>();
    }*/
}
