package fpl.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransferDto(
        int elementIn,
        int elementOut,
        int event
) {}
