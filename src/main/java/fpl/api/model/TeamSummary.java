package fpl.api.model;

import java.util.List;

public record TeamSummary(
        int count,
        int tripleCaptain,
        int wildcard,
        int benchBoost,
        int freeHit,
        List<Player> players
) {
}
