package fpl.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pick(
        int element,
        int position,
        int multiplier,
        boolean isCaptain,
        boolean isViceCaptain,
        int elementType
) {}
