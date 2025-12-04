package fpl.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Event(
        int id,
        boolean isPrevious,
        boolean isCurrent
) {}
