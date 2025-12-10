package fpl.domain.transfers;

public record Transfer(
        String playerIn,
        String playerOut,
        boolean wildcard,
        boolean freeHit
) {}
