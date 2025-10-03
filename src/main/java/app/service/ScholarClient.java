package app.service;

import app.model.Author;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScholarClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Author> searchAuthors(String query) throws IOException {
        String apiBase = System.getenv("SCHOLAR_API_BASE");
        if (apiBase == null || apiBase.isBlank()) {
            // Mock data so the app works offline
            List<Author> mock = new ArrayList<>();
            mock.add(new Author("a1","Ada Lovelace","Analytical Engine Institute", 12000, 45));
            mock.add(new Author("a2","Alan Turing","Bletchley Park", 22000, 60));
            return mock;
        }

        String url = apiBase + URLEncoder.encode(query, StandardCharsets.UTF_8);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            // Recommended in HttpClient 5.x: use a response handler
            HttpClientResponseHandler<List<Author>> handler = response -> {
                int status = response.getCode();
                String respBody = response.getEntity() != null
                        ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                        : "";

                if (status >= 200 && status < 300) {
                    JsonNode root = mapper.readTree(respBody);
                    List<Author> out = new ArrayList<>();

                    // Expecting a JSON array; also support wrappers like { "items": [...] }
                    if (root.isArray()) {
                        for (JsonNode n : root) {
                            out.add(toAuthor(n));
                        }
                    } else if (root.has("items") && root.get("items").isArray()) {
                        for (JsonNode n : root.get("items")) {
                            out.add(toAuthor(n));
                        }
                    } else {
                        // Single object fallback
                        out.add(toAuthor(root));
                    }
                    return out;
                } else {
                    throw new IOException("Remote API error: HTTP " + status + " - " + respBody);
                }
            };

            return client.execute(request, handler);
        }
    }

    // Small helper to map a JSON node to your Author model
    private Author toAuthor(JsonNode n) {
        return new Author(
                n.path("id").asText(null),
                n.path("name").asText(null),
                n.path("affiliation").asText(null),
                n.path("citations").isNumber() ? n.get("citations").asInt() : null,
                n.path("hIndex").isNumber()   ? n.get("hIndex").asInt()   : null
        );
    }
}
