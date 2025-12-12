package fpl.excel.style;

import java.util.Map;

public class PlayerColorMapper {

    public static final Map<Integer, Color> PLAYER_COLORS = Map.of(
            1, Color.LIGHT_ORANGE,
            2, Color.LIGHT_YELLOW,
            3, Color.LIGHT_TURQUOISE,
            4, Color.LIGHT_CORNFLOWER_BLUE
    );

    public static Color getColorForPlayer(int position) {
        return PLAYER_COLORS.getOrDefault(position, Color.LIGHT_YELLOW);
    }
}
