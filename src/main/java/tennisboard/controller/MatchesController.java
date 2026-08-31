package tennisboard.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tennisboard.dto.FinishedMatchesEssentialInfoDto;
import tennisboard.dto.MatchSnapshot;
import tennisboard.mapper.MatchResponseMapper;
import tennisboard.request.CreateMatchRequest;
import tennisboard.request.UpdateMatchRequest;
import tennisboard.response.CreateMatchResponse;
import tennisboard.service.MatchService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/matches")
public class MatchesController {
    private final MatchService matchService;

    private final MatchResponseMapper mapper;

    @PostMapping
    public ResponseEntity<CreateMatchResponse> createNewMatch(@RequestBody @Valid CreateMatchRequest request) {
        String firstPlayerName = request.firstPlayerName();
        String secondPlayerName = request.secondPlayerName();
        UUID matchId = matchService.createNewMatch(firstPlayerName, secondPlayerName);

        return new ResponseEntity<>(
                mapper.toCreateMatchResponse(matchId),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<MatchSnapshot> getMatchStats(@PathVariable("uuid") UUID uuid) {
        MatchSnapshot snapshot = matchService.getMatchSnapshot(uuid);

        return new ResponseEntity<>(
                snapshot,
                HttpStatus.OK
        );
    }

    @PostMapping("/{uuid}/point")
    public ResponseEntity<MatchSnapshot> addPoint(
            @RequestBody @Valid UpdateMatchRequest request,
            @PathVariable("uuid") UUID uuid
    ) {
        String name = request.name();
        MatchSnapshot snapshot = matchService.addPoint(name, uuid);

        return new ResponseEntity<>(
                snapshot,
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<FinishedMatchesEssentialInfoDto> getFinishedMatches(
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "player_name", required = false) @Size(max = 100) String playerName
    ) {
        FinishedMatchesEssentialInfoDto finishedMatches
                = matchService.getFinishedMatches(page, playerName);

        return new ResponseEntity<>(
                finishedMatches,
                HttpStatus.OK
        );
    }
}
