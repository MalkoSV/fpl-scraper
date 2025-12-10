package fpl.domain.utils;

import fpl.domain.transfers.Transfer;

import java.util.List;

public class TransfersFilter {

    public static List<Transfer> filterWildcard(List<Transfer> transfers) {
        return transfers.stream()
                .filter(Transfer::wildcard)
                .toList();
    }

    public static List<Transfer> filterFreeHit(List<Transfer> transfers) {
        return transfers.stream()
                .filter(Transfer::freeHit)
                .toList();
    }

    public static List<Transfer> filterWithoutFreeHit(List<Transfer> transfers) {
        return transfers.stream()
                .filter(tr -> !tr.freeHit())
                .toList();
    }

}
