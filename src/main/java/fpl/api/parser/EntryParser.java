package fpl.api.parser;

import fpl.api.FplApiEndPoints;
import fpl.api.model.BootstrapResponse;
import fpl.api.model.EntryHistory;
import fpl.api.model.EntryResponse;
import fpl.api.model.Event;
import fpl.api.model.Pick;
import fpl.api.model.PlayerDto;

import java.net.URI;
import java.util.List;

public class EntryParser {

    public static EntryResponse parseEntry(URI uri) throws Exception {
        String json = JsonUtils.loadJsonFromUri(uri);

        return JsonUtils.MAPPER.readValue(json, EntryResponse.class);
    }

    public static List<Pick> getPicks(EntryResponse response) throws Exception {
        return response.picks();
    }

    public static String getActiveChip(EntryResponse response) throws Exception {
        return response.activeChip();
    }

    public static int getEventTransfers(EntryResponse response) throws Exception {
        return response.entryHistory().eventTransfers();
    }

    public static int getEventTransfersCost(EntryResponse response) throws Exception {
        return response.entryHistory().eventTransfersCost();
    }

}
