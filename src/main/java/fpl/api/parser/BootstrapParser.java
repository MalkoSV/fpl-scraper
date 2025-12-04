package fpl.api.parser;

import fpl.api.FplApiEndPoints;
import fpl.api.model.dto.BootstrapResponse;
import fpl.api.model.dto.Event;
import fpl.api.model.dto.PlayerDto;

import java.util.List;

public class BootstrapParser {

    public static BootstrapResponse parseBootstrap() throws Exception {
        var uri = FplApiEndPoints.getUri(FplApiEndPoints.BOOTSTRAP);
        String json = JsonUtils.loadJsonFromUri(uri);

        return JsonUtils.MAPPER.readValue(json, BootstrapResponse.class);
    }

    public static List<PlayerDto> getPlayers(BootstrapResponse response) throws Exception {
        return response.elements();
    }

    public static List<Event> getEvents(BootstrapResponse response) throws Exception {
        return response.events();
    }

    public static int getLastEvent(BootstrapResponse response) throws Exception {
        return getEvents(response).stream()
                .filter(e -> e.isCurrent() || e.isPrevious())
                .mapToInt(Event::id)
                .max()
                .orElse(0);
    }
}
