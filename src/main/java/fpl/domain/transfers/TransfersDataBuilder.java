package fpl.domain.transfers;

import fpl.domain.utils.TransferUtils;
import fpl.domain.utils.TransfersFilter;

import java.util.List;

public class TransfersDataBuilder {

    public TransfersData build(List<Transfer> transfers) {
        List<Transfer> withoutFreeHit = TransfersFilter.filterWithoutFreeHit(transfers);
        List<Transfer> wildcard = TransfersFilter.filterWildcard(transfers);
        List<Transfer> freeHit = TransfersFilter.filterFreeHit(transfers);

        return new TransfersData(
                TransferUtils.calculateTransfersIn(withoutFreeHit),
                TransferUtils.calculateTransfersOut(withoutFreeHit),
                TransferUtils.calculateTransfersIn(wildcard),
                TransferUtils.calculateTransfersOut(wildcard),
                TransferUtils.calculateTransfersIn(freeHit),
                TransferUtils.calculateTransfersOut(freeHit)
        );
    }
}
