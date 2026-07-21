package tennisboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tennisboard.dto.MatchSnapshot;
import tennisboard.dto.ShortMatchInfoDTO;
import tennisboard.entity.MatchEntity;
import tennisboard.mapper.calculator.MatchScoreFieldsCalculator;
import tennisboard.service.logic.Match;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = MatchScoreFieldsCalculator.class
)
public interface MatchInternalMapper {

    @Mapping(source = "player1.name", target = "firstPlayerName")
    @Mapping(source = "player2.name", target = "secondPlayerName")
    @Mapping(source = "matchScore.gameA", target = "firstPlayerGames")
    @Mapping(source = "matchScore.gameB", target = "secondPlayerGames")
    @Mapping(target = "firstPlayerPoints", source = ".", qualifiedByName = "firstPlayerPoints")
    @Mapping(target = "secondPlayerPoints", source = ".", qualifiedByName = "secondPlayerPoints")
    @Mapping(source = "matchScore.setA", target = "firstPlayerSets")
    @Mapping(source = "matchScore.setB", target = "secondPlayerSets")
    @Mapping(source = "matchScore.tieBreakPointA", target = "firstPlayerTieBreakPoints")
    @Mapping(source = "matchScore.tieBreakPointB", target = "secondPlayerTieBreakPoints")
    @Mapping(target = "winnerName", source = ".", qualifiedByName = "winnerName")
    MatchSnapshot toMatchSnapshot(Match match);

    @Mapping(source = "firstPlayer.name", target = "firstPlayerName")
    @Mapping(source = "secondPlayer.name", target = "secondPlayerName")
    @Mapping(source = "winner.name", target = "winnerName")
    ShortMatchInfoDTO toShortMatchInfoDTO(MatchEntity matchEntity);

    List<ShortMatchInfoDTO> toShortMatchInfoDTOList(List<MatchEntity> entities);
}
