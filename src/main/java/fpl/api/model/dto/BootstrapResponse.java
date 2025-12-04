package fpl.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BootstrapResponse(
        List<PlayerDto> elements,
        List<Event> events
) {}
