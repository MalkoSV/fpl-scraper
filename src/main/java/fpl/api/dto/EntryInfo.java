package fpl.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EntryInfo(
        int eventTotal,
        String playerName,
        int rank,
        int total,
        int entry,
        String entryName
) {}

