package tennisboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tennisboard.response.CreateMatchResponse;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface MatchResponseMapper {
    @Mapping(source = "uuid", target = "id")
    CreateMatchResponse toCreateMatchResponse(UUID uuid);
}
