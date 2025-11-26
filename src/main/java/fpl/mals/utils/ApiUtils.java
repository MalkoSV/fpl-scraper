package fpl.mals.utils;

import fpl.mals.Team;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiUtils {
    private static final String FPL_API_BASE = "https://fantasy.premierleague.com/api";
    public static final URI FPL_API_BOOTSTRAP_URI = URI.create(FPL_API_BASE + "/bootstrap-static/");
    public static final int OVERALL_LEAGUE_ID = 314;
    public static final int MALS_LEAGUE_ID = 1032011;
    public static final int PROGNOZILLA_LEAGUE_ID = 1031449;

    public static URI getApiTeamUri(int entry, int event) {
        return URI.create("%s/entry/%d/event/%d/picks/".formatted(FPL_API_BASE, entry, event));
    }

    public static URI getApiLeagueUri(int id, int page) {
        return URI.create("%s/leagues-classic/%d/standings/?page_standings=%d".formatted(FPL_API_BASE, id, page));
    }

    public static int getStandingsID(int mode) {
        return switch (mode) {
            case 21 -> MALS_LEAGUE_ID;
            case 22 -> PROGNOZILLA_LEAGUE_ID;
            default -> OVERALL_LEAGUE_ID;
        };
    }

    public static String loadJsonFromUri(URI uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static List<Team> collectStats(List<URI> teams) {

        return List.of();
    }
}
