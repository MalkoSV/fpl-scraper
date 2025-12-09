package fpl.parser;

import fpl.api.dto.EntryResponse;
import fpl.api.dto.Pick;

import java.net.URI;
import java.util.List;

public class EntryParser {

    public static EntryResponse parse(URI uri) throws Exception {
        String json = JsonUtils.loadJsonFromUri(uri);

        return JsonUtils.MAPPER.readValue(json, EntryResponse.class);
    }

    public static List<Pick> getPicks(EntryResponse response) {
        return response.picks();
    }

    public static String getActiveChip(EntryResponse response) {
        return response.activeChip();
    }

    public static int getEventTransfers(EntryResponse response) {
        return response.entryHistory().eventTransfers();
    }

    public static int getEventTransfersCost(EntryResponse response) {
        return response.entryHistory().eventTransfersCost();
    }

    public static int getPoints(EntryResponse response) throws Exception {
        return response.entryHistory().points();
    }

    public static int getBank(EntryResponse response) {
        return response.entryHistory().bank();
    }

    public static int getValue(EntryResponse response) {
        return response.entryHistory().value();
    }

    public static int getPointsOnBench(EntryResponse response) {
        return response.entryHistory().pointsOnBench();
    }

}
