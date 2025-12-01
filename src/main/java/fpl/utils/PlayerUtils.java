package fpl.utils;

import fpl.web.model.Player;

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
                                Math.min(p1.getPoints(), p2.getPoints()),
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

    public static List<Player> getOnlyStartPlayers(List<Player> players) {
        return players.stream()
                .filter(p -> p.getStart() == p.getCount())
                .toList();
    }

    public static List<Player> getOnlyBenchPlayers(List<Player> players) {
        return players.stream()
                .filter(p -> p.getStart() == 0)
                .toList();
    }

    public static List<Player> getDoubtfulPlayers(List<Player> players) {
        return players.stream()
                .filter(p -> p.getAvailability() <= 50)
                .toList();
    }

    public static List<Player> getBenchPlayersWithHighPoints(List<Player> players) {
        return players.stream()
                .filter(p -> p.getCount() - p.getStart() > 0 && p.getPoints() > 5)
                .toList();
    }

    public static List<Player> getPlayersWhoCaptain(List<Player> players) {
        return players.stream()
                .filter(p -> p.getCaptain() > 0)
                .peek((p) -> p.setPoints(p.getPoints() * 2))
                .toList();
    }

    public static long countStartPlayersWithZero(List<Player> players) {
        return players.stream()
                .filter(p -> p.getStart() == 1 && p.getPoints() <= 0)
                .count();
    }
}
