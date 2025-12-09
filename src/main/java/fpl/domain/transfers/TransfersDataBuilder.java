package fpl.domain.transfers;

import fpl.domain.utils.TransferUtils;

import java.util.List;

public class TransfersDataBuilder {

    public TransfersData build(List<Transfer> transfers) {

        return new TransfersData(
                TransferUtils.calculateTransfersIn(transfers),
                TransferUtils.calculateTransfersOut(transfers)
        );
    }
}
