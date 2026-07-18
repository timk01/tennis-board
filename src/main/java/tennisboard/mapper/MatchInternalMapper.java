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
    @Mapping(target = "secondPlayerPoints",  source = ".", qualifiedByName = "secondPlayerPoints")
    @Mapping(source = "matchScore.setA", target = "firstPlayerSets")
    @Mapping(source = "matchScore.setB", target = "secondPlayerSets")
    @Mapping(source = "matchScore.tieBreakPointA", target = "firstPlayerTieBreakPoints")
    @Mapping(source = "matchScore.tieBreakPointB", target = "secondPlayerTieBreakPoints")
    @Mapping(target = "winnerName",  source = ".", qualifiedByName = "winnerName")
    MatchSnapshot toMatchSnapshot(Match match);

    //с хибером дальше (ссылочки)
    @Mapping(source = "player1", target = "firstPlayerName") //player1.name
    @Mapping(source = "player2", target = "secondPlayerName")  //player2.name
    @Mapping(source = "winner", target = "winnerName")  //winner.name
    ShortMatchInfoDTO toShortMatchInfoDTO(MatchEntity matchEntity);

    List<ShortMatchInfoDTO> toShortMatchInfoDTOList(List<MatchEntity> entities);
}
