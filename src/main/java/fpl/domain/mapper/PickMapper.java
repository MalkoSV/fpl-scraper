package fpl.domain.mapper;

import fpl.api.dto.Pick;
import fpl.domain.model.PositionType;

public class PickMapper {

    public static PositionType toPositionType(Pick pick) {
        return PositionType.fromCode(pick.elementType());
    }

    public static boolean isBench(Pick pick) {
        return pick.multiplier() == 0;
    }
}
