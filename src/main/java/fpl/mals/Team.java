package fpl.mals;

import fpl.mals.utils.PlayerUtils;

import java.util.List;
import java.util.stream.Stream;

public record Team(
        String name,
        int tripleCaptain,
        int wildCard,
        int benchBoost,
        int freeHit,
        int transfers,
        List<Player> goalkeeper,
        List<Player> defenders,
        List<Player> midfielders,
        List<Player> offenders,
        List<Player> bench) {

    public static long countStartPlayersWithZero(Team t) {
        return PlayerUtils.countStartPlayersWithZeroInList(t.goalkeeper())
                + PlayerUtils.countStartPlayersWithZeroInList(t.defenders())
                + PlayerUtils.countStartPlayersWithZeroInList(t.midfielders())
                + PlayerUtils.countStartPlayersWithZeroInList(t.offenders())
                + PlayerUtils.countStartPlayersWithZeroInList(t.bench());
    }

    public Stream<Player> streamPlayers() {
        return Stream.of(
                        goalkeeper,
                        defenders,
                        midfielders,
                        offenders,
                        bench
                )
                .flatMap(List::stream);
    }

}
