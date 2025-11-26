package fpl.mals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Standings(
        int eventTotal,
        String playerName,
        int rank,
        int total,
        int entry,
        String entryName,
        String news
) {}

