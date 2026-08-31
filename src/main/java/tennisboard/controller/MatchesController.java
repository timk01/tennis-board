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
import tennisboard.response.FinishedMatchesEssentialInfoResponse;
import tennisboard.response.MatchScoreResponse;
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
        String name1 = request.firstPlayerName();
        String name2 = request.secondPlayerName();
        UUID matchId = matchService.createNewMatch(name1, name2);

        return new ResponseEntity<>(
                mapper.toCreateMatchResponse(matchId),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<MatchScoreResponse> getMatchStats(@PathVariable("uuid") UUID uuid) {
        MatchSnapshot snapshot = matchService.getMatchSnapshot(uuid);

        return new ResponseEntity<>(
                mapper.toMatchScoreResponse(snapshot),
                HttpStatus.OK);
    }

    @PostMapping("/{uuid}/point")
    public ResponseEntity<MatchScoreResponse> addPoint(
            @RequestBody @Valid UpdateMatchRequest request,
            @PathVariable("uuid") UUID uuid
    ) {
        String name = request.name();
        MatchSnapshot snapshot = matchService.addPoint(name, uuid);

        return new ResponseEntity<>(
                mapper.toMatchScoreResponse(snapshot),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<FinishedMatchesEssentialInfoResponse> getFinishedMatches(
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "player_name", required = false) @Size(max = 100) String playerName
    ) {
        FinishedMatchesEssentialInfoDto finishedMatches
                = matchService.getFinishedMatches(page, playerName);

        return new ResponseEntity<>(
                mapper.toFinishedMatchesEssentialInfoResponse(finishedMatches),
                HttpStatus.OK);
    }
}
