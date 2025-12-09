package fpl.parser;

import fpl.api.FplApiEndPoints;
import fpl.api.dto.BootstrapResponse;
import fpl.api.dto.Event;
import fpl.api.dto.PlayerDto;

import java.util.List;

public class BootstrapParser {

    public static BootstrapResponse parse() throws Exception {
        var uri = FplApiEndPoints.getUri(FplApiEndPoints.BOOTSTRAP);
        String json = JsonUtils.loadJsonFromUri(uri);

        return JsonUtils.MAPPER.readValue(json, BootstrapResponse.class);
    }

    public static List<PlayerDto> getPlayers(BootstrapResponse response) {
        return response.elements();
    }

    public static List<Event> getEvents(BootstrapResponse response) {
        return response.events();
    }

    public static int getLastEvent(BootstrapResponse response) {
        return getEvents(response).stream()
                .filter(e -> e.isCurrent() || e.isPrevious())
                .mapToInt(Event::id)
                .max()
                .orElse(0);
    }
}
