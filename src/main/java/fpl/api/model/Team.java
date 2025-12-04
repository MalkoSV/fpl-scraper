package fpl.api.model;

import fpl.utils.PlayerUtils;

import java.util.List;
import java.util.stream.Stream;

public record Team(
        int tripleCaptain,
        int wildCard,
        int benchBoost,
        int freeHit,
        int transfers,
        int transfersCost,
        List<Player> goalkeeper,
        List<Player> defenders,
        List<Player> midfielders,
        List<Player> forwards,
        List<Player> bench) {

    public static long countStartPlayersWithZero(Team t) {
        return PlayerUtils.countStartPlayersWithZero(t.goalkeeper())
                + PlayerUtils.countStartPlayersWithZero(t.defenders())
                + PlayerUtils.countStartPlayersWithZero(t.midfielders())
                + PlayerUtils.countStartPlayersWithZero(t.forwards())
                + PlayerUtils.countStartPlayersWithZero(t.bench());
    }

    public Stream<Player> streamPlayers() {
        return Stream.of(
                        goalkeeper,
                        defenders,
                        midfielders,
                        forwards,
                        bench
                )
                .flatMap(List::stream);
    }

}
