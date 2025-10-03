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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScholarClient {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Author> searchAuthors(String query) throws IOException, ParseException {
        String apiBase = System.getenv("SCHOLAR_API_BASE");
        if (apiBase == null || apiBase.isBlank()) {
            List<Author> mock = new ArrayList<>();
            mock.add(new Author("a1","Ada Lovelace","Analytical Engine Institute", 12000, 45));
            mock.add(new Author("a2","Alan Turing","Bletchley Park", 22000, 60));
            return mock;
        }
        String url = apiBase + URLEncoder.encode(query, StandardCharsets.UTF_8);
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(url);
            try (ClassicHttpResponse resp = http.execute(req)) {
                int code = resp.getCode();
                String body = resp.getEntity() != null ? EntityUtils.toString(resp.getEntity()) : "";
                if (code >= 200 && code < 300) {
                    JsonNode arr = mapper.readTree(body);
                    List<Author> out = new ArrayList<>();
                    if (arr.isArray()) {
                        for (JsonNode n : arr) {
                            Author a = new Author(
                                n.path("id").asText(null),
                                n.path("name").asText(null),
                                n.path("affiliation").asText(null),
                                n.path("citations").isNumber() ? n.get("citations").asInt() : null,
                                n.path("hIndex").isNumber() ? n.get("hIndex").asInt() : null
                            );
                            out.add(a);
                        }
                    }
                    return out;
                } else {
                    throw new IOException("Remote API error: HTTP " + code + " - " + body);
                }
            }
        }
    }
}
