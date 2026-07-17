package tennisboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tennisboard.mapper.calculator.MatchScoreFieldsCalculator;
import tennisboard.response.CreateMatchResponse;
import tennisboard.response.MatchScoreResponse;
import tennisboard.service.logic.Match;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        uses = MatchScoreFieldsCalculator.class
)
public interface MatchResponseMapper {
    @Mapping(source = "uuid", target = "id")
    CreateMatchResponse toCreateMatchResponse(UUID uuid);

    @Mapping(source = "player1.name", target = "firstPlayerName")
    @Mapping(source = "player2.name", target = "secondPlayerName")
    @Mapping(source = "matchScore.gameA", target = "firstPlayerGames")
    @Mapping(source = "matchScore.gameB", target = "secondPlayerGames")
    @Mapping(target = "firstPlayerPoints", source = ".", qualifiedByName = "firstPlayerPoints")
    @Mapping(target = "secondPlayerPoints",  source = ".", qualifiedByName = "secondPlayerPoints")
    @Mapping(source = "matchScore.setA", target = "firstPlayerSets")
    @Mapping(source = "matchScore.setB", target = "secondPlayerSets")
    @Mapping(source = "matchScore.tieBreakPointA", target = "firstPlayerTieBreakPoints")
    @Mapping(source = "matchScore.tieBreakPointB", target = "secondPlayerTieBreakPoints")
    @Mapping(target = "winnerName",  source = ".", qualifiedByName = "winnerName")
    MatchScoreResponse toMatchScoreResponse(Match match);
}
