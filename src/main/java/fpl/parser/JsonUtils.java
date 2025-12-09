package fpl.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class JsonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private JsonUtils() {
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

    public static <T> List<T> loadList(URI uri, Class<T> clazz) throws Exception {
        String json = loadJsonFromUri(uri);
        var type = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);

        return MAPPER.readValue(json, type);
    }
}
