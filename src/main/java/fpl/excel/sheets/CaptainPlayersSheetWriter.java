package fpl.excel.sheets;

import fpl.api.model.Player;
import fpl.excel.builder.Col;
import fpl.excel.builder.TableSheetWriter;

import java.util.List;

public class CaptainPlayersSheetWriter extends TableSheetWriter<Player> {

    private static final List<Col<Player>> COLUMNS = List.of(
            new Col<>("Name", Player::getName),
            new Col<>("Captain", Player::getCaptain),
            new Col<>("Points", Player::getPoints)
    );

    public CaptainPlayersSheetWriter(List<Player> players) {
        super("Captain", players, COLUMNS);
    }
}
