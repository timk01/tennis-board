package tennisboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tennisboard.response.CreateMatchResponse;

import java.util.UUID;

@Mapper(
        componentModel = "spring"
)
public interface MatchResponseMapper {
    @Mapping(source = "uuid", target = "id")
    CreateMatchResponse toCreateMatchResponse(UUID uuid);

}
