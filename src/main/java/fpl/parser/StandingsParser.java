package fpl.parser;

import fpl.api.FplApiEndPoints;
import fpl.api.dto.LeagueResponse;
import fpl.api.dto.EntryInfo;
import fpl.api.InputUtils;

import java.util.ArrayList;
import java.util.List;

public class StandingsParser {

    private final int mode;

    private StandingsParser(int mode) {
        this.mode = mode;
    }

    public static StandingsParser of(int mode) {
        return new StandingsParser(mode);
    }

    public List<EntryInfo> parse() throws Exception {
        List<EntryInfo> list = new ArrayList<>();
        int totalPages = getTotalPages();

        for (int page = 1; page <= totalPages; page++) {
            var uri = FplApiEndPoints.getUri(
                    FplApiEndPoints.LEAGUE,
                    FplApiEndPoints.getLeagueId(mode),
                    page
            );

            String json = JsonUtils.loadJsonFromUri(uri);
            LeagueResponse response = JsonUtils.MAPPER.readValue(json, LeagueResponse.class);

            list.addAll(response.standings().results());
        }

        return list;
    }

    private int getTotalPages() {
        return mode <= InputUtils.MAX_PAGES ? mode : 1;
    }
}
