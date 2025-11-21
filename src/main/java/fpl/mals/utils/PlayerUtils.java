package fpl.mals.utils;

import fpl.mals.Player;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerUtils {
    public static List<Player> mergePlayers(List<Player> players) {
        return players.stream()
                .collect(Collectors.toMap(
                        Player::getName,
                        Function.identity(),
                        (p1, p2) -> new Player(
                                p1.getName(),
                                p1.getCount() + p2.getCount(),
                                p1.getStart() + p2.getStart(),
                                p1.getCaptain() + p2.getCaptain(),
                                p1.getTripleCaptain() + p2.getTripleCaptain(),
                                p1.getVice() + p2.getVice(),
                                Math.min(p1.getScore(), p2.getScore()),
                                p1.getAvailability()
                        )
                ))
                .values()
                .stream()
                .sorted(
                        Comparator.comparing(Player::getCount).reversed()
                                .thenComparing(Player::getName)
                )
                .toList();
    }
}
