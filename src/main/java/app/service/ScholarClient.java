package app.service;

import app.model.PlaceResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarClient {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${serp.api.base}")
    private String apiBase;

    @Value("${serp.api.key}")
    private String apiKey;

    public List<PlaceResult> searchPlaces(String query) throws IOException {
        if (apiBase == null || apiBase.isBlank()) {
            return List.of(
                    PlaceResult.builder().title("Ada Lovelace Coffee").type("Mock Shop").address("London").build(),
                    PlaceResult.builder().title("Alan Turing Cafe").type("Mock Shop").address("Manchester").build()
            );
        }

        String url = apiBase
                + "?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&engine=google"
                + "&api_key=" + apiKey;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            HttpClientResponseHandler<List<PlaceResult>> handler = response -> {
                int status = response.getCode();
                String respBody = response.getEntity() != null
                        ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                        : "";

                if (status >= 200 && status < 300) {
                    JsonNode root = mapper.readTree(respBody);
                    JsonNode places = root.path("local_results").path("places");
                    List<PlaceResult> out = new ArrayList<>();
                    if (places.isArray()) {
                        for (JsonNode n : places) {
                            out.add(toPlace(n));
                        }
                    }
                    return out;
                } else {
                    throw new IOException("Remote API error: HTTP " + status + " - " + respBody);
                }
            };

            return client.execute(request, handler);
        }
    }

    private PlaceResult toPlace(JsonNode n) {
        return PlaceResult.builder()
                .title(n.path("title").asText(null))
                .type(n.path("type").asText(null))
                .address(n.path("address").asText(null))
                .build();
    }
}
