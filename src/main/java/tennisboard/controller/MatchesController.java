package tennisboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tennisboard.service.HelloService;
import tennisboard.service.MatchService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/matches")
public class MatchesController {
    private final MatchService matchService;

    public MatchesController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<UUID> createNewMatch(@RequestBody PlayersName playersName) { //uuid или скорее Жсон ?
        return new ResponseEntity<>(UUID.randomUUID(),  HttpStatus.OK);
        matchService.createNewMatch(потроха из рекорда выше);
    }

    @PostMapping("/{uuid}/point")
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
    }
}
