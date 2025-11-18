package fpl.mals;

import java.util.List;
import java.util.stream.Stream;

public class Team {
    private String name;
    private String position;
    private final int tripleCaptain;
    private final int wildCard;
    private final int benchBoost;
    private final int freeHit;

    private final List<Player> goalkeeper;
    private List<Player> defenders;
    private List<Player> midfielders;
    private List<Player> offenders;
    private List<Player> bench;

    public Team(String name, String position, int tripleCaptain, int wildCard, int benchBoost, int freeHit,
                List<Player> goalkeeper, List<Player> defenders, List<Player> midfielders, List<Player> offenders, List<Player> bench) {
        this.name = name;
        this.position = position;
        this.tripleCaptain = tripleCaptain;
        this.wildCard = wildCard;
        this.benchBoost = benchBoost;
        this.freeHit = freeHit;
        this.goalkeeper = goalkeeper;
        this.defenders = defenders;
        this.midfielders = midfielders;
        this.offenders = offenders;
        this.bench = bench;
    }

    public Team(int tripleCaptain, int wildCard, int benchBoost, int freeHit, List<Player> goalkeeper) {
        this.tripleCaptain = tripleCaptain;
        this.wildCard = wildCard;
        this.benchBoost = benchBoost;
        this.freeHit = freeHit;
        this.goalkeeper = goalkeeper;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getTripleCaptain() {
        return tripleCaptain;
    }

    public int getWildCard() {
        return wildCard;
    }

    public int getBenchBoost() {
        return benchBoost;
    }

    public int getFreeHit() {
        return freeHit;
    }

    public List<Player> getGoalkeeper() {
        return goalkeeper;
    }

    public List<Player> getDefenders() {
        return defenders;
    }

    public List<Player> getMidfielders() {
        return midfielders;
    }

    public List<Player> getOffenders() {
        return offenders;
    }

    public List<Player> getBench() {
        return bench;
    }

    public Stream<Player> streamPlayers() {
        return Stream.of(
                goalkeeper,
                defenders,
                midfielders,
                offenders,
                bench
        ).flatMap(List::stream);
    }
}
