package fpl.domain.transfers;

import java.util.Map;

public record TransfersData(
        Map<String, Long> transfersIn,
        Map<String, Long> transfersOut
) {}
