package fpl.mals.utils;

import fpl.mals.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerUtils {
    public static List<Player> mergePlayers(List<Player> players) {
        return players.stream()
                .collect(Collectors.toMap(
                        Player::getName,
                        p -> new Player(p.getName(), p.getCount(), p.getStart(), p.getCaptain(), p.getVice(), p.getScore()),
                        (p1, p2) -> {
                            p1.setCount(p1.getCount() + p2.getCount());
                            p1.setStart(p1.getStart() + p2.getStart());
                            p1.setCaptain(p1.getCaptain() + p2.getCaptain());
                            p1.setVice(p1.getVice() + p2.getVice());
                            p1.setScore(Math.min(p1.getScore(), p2.getScore()));
                            return p1;
                        }))
                .values()
                .stream()
                .sorted(
                        Comparator.comparing(Player::getCount).reversed()
                                .thenComparing(Player::getName)
                )
                .toList();
    }
}
