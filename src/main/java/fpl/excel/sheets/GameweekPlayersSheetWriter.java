package fpl.excel.sheets;

import fpl.api.model.Player;
import fpl.excel.builder.Col;
import fpl.excel.builder.TableSheetWriter;

import java.util.List;

public class GameweekPlayersSheetWriter extends TableSheetWriter<Player> {

    private static final List<Col<Player>> COLUMNS = List.of(
            new Col<>("Name", Player::getName),
            new Col<>("Count", Player::getCount),
            new Col<>("Start", Player::getStart),
            new Col<>("Captain", Player::getCaptain),
            new Col<>("Triple", Player::getTripleCaptain),
            new Col<>("Vice", Player::getVice),
            new Col<>("Bench", p -> p.getCount() - p.getStart()),
            new Col<>("Availability", Player::getAvailability),
            new Col<>("Points", Player::getPoints)
    );

    public GameweekPlayersSheetWriter(List<Player> players) {
        super("Gameweek players", players, COLUMNS);
    }
}
