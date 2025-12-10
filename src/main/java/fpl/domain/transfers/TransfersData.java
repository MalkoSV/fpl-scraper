package fpl.domain.transfers;

import java.util.Map;

public record TransfersData(
        Map<String, Long> withoutFreeHitIn,
        Map<String, Long> withoutFreeHitOut,
        Map<String, Long> wildcardIn,
        Map<String, Long> wildcardOut,
        Map<String, Long> freeHitIn,
        Map<String, Long> freeHitOut
) {}
