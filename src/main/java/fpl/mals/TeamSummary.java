package fpl.mals;

import java.util.List;

public record TeamSummary(
        int count,
        int tripleCaptain,
        int wildcard,
        int freeHit,
        int benchBoost,
        List<Player> players
) {
}
