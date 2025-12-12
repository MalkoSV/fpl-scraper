package fpl.excel.style;

import fpl.domain.model.HasPosition;

public class ColorUtils {

    private ColorUtils() {}

    public static Color getColorForCell(HasPosition item) {
        return PlayerColorMapper.getColorForPlayer(item.getPosition());
    }
}
