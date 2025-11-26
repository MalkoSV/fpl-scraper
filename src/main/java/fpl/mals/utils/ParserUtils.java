package fpl.mals.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import fpl.mals.PlayerElement;
import fpl.mals.Standings;

import java.util.ArrayList;
import java.util.List;

public class ParserUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static List<PlayerElement> parsePlayerElements() throws Exception {
        String json = ApiUtils.loadJsonFromUri(ApiUtils.FPL_API_BOOTSTRAP_URI);
        JsonNode root = MAPPER.readTree(json);
        JsonNode elements = root.get("elements");

        List<PlayerElement> list = new ArrayList<>();

        for (JsonNode el : elements) {
            list.add(MAPPER.treeToValue(el, PlayerElement.class));
        }

        return list;
    }

    public static List<Standings> parseStandings(int mode) throws Exception {
        int n = mode <= 20 ? mode : 1;

        List<Standings> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            String json = ApiUtils.loadJsonFromUri(ApiUtils.getApiLeagueUri(ApiUtils.getStandingsID(mode), i));
            JsonNode root = MAPPER.readTree(json);
            JsonNode elements = root.get("elements");

            for (JsonNode el : elements) {
                list.add(MAPPER.treeToValue(el, Standings.class));
            }
        }
        return list;
    }

}
