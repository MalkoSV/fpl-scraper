package fpl.excel.sheets;

import fpl.api.model.Player;
import fpl.excel.builder.Col;
import fpl.excel.builder.TableSheetWriter;

import java.util.List;

public class DoubtfulPlayersSheetWriter extends TableSheetWriter<Player> {

    private static final List<Col<Player>> COLUMNS = List.of(
            new Col<>("Name", Player::getName),
            new Col<>("Count", Player::getCount),
            new Col<>("Start", Player::getStart),
            new Col<>("Availability", Player::getAvailability),
            new Col<>("Points", Player::getPoints)
    );

    public DoubtfulPlayersSheetWriter(List<Player> players) {
        super("Doubtful", players, COLUMNS);
    }
}
