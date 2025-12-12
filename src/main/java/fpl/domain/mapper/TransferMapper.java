package fpl.domain.mapper;

import fpl.api.dto.PlayerDto;
import fpl.api.dto.TransferDto;
import fpl.domain.model.Team;
import fpl.domain.transfers.Transfer;

import java.util.Map;

public class TransferMapper {

    public static Transfer toDomain(
            TransferDto dto,
            Map<Integer, PlayerDto> playersById,
            Map<Integer, Team> teamsByEntry
    ) {
        String inName = playersById.get(dto.elementIn()).webName();
        String outName = playersById.get(dto.elementOut()).webName();

        Team team = teamsByEntry.get(dto.entry());

        boolean wildcard = team.wildCard() > 0;
        boolean freeHit = team.freeHit() > 0;

        return new Transfer(
                inName,
                outName,
                wildcard,
                freeHit
        );
    }
}
