package fpl.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EntryHistory(
        int event,
        int points,
        int eventTransfers,
        int eventTransfersCost
) {}
