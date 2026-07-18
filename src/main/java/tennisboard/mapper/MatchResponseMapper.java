package tennisboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tennisboard.dto.MatchSnapshot;
import tennisboard.mapper.calculator.MatchScoreFieldsCalculator;
import tennisboard.response.CreateMatchResponse;
import tennisboard.response.MatchScoreResponse;

import java.util.UUID;

@Mapper(
        componentModel = "spring"
)
public interface MatchResponseMapper {
    @Mapping(source = "uuid", target = "id")
    CreateMatchResponse toCreateMatchResponse(UUID uuid);

    MatchScoreResponse toMatchScoreResponse(MatchSnapshot matchSnapshot);
}
