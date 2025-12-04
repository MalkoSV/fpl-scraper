package fpl.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamStats(
        int eventTotal,
        String playerName,
        int rank,
        int total,
        int entry,
        String entryName
) {}

