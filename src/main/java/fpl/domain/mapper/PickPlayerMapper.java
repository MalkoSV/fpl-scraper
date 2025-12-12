package fpl.domain.mapper;

import fpl.api.dto.Pick;
import fpl.api.dto.PlayerDto;
import fpl.domain.model.Player;
import fpl.domain.utils.BoolUtils;

import java.util.Map;

public class PickPlayerMapper {

    private PickPlayerMapper() {}

    public static Player fromPick(Pick pickDto, Map<Integer, PlayerDto> playersById) {
        PlayerDto playerDto = playersById.get(pickDto.element());

        return new Player(
                playerDto.webName(),
                1,
                BoolUtils.asInt(pickDto.multiplier() > 0),
                BoolUtils.asInt(pickDto.multiplier() >= 2),
                BoolUtils.asInt(pickDto.multiplier() == 3),
                BoolUtils.asInt(pickDto.isViceCaptain()),
                playerDto.eventPoints(),
                playerDto.chanceSafe(),
                playerDto.elementType()
        );
    }
}
