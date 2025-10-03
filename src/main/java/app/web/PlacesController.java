package app.web;

import app.model.PlaceResult;
import app.service.ScholarClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class PlacesController {
    private final ScholarClient scholarClient;

    public PlacesController(ScholarClient scholarClient) {
        this.scholarClient = scholarClient;
    }

    @GetMapping({"/", "/places"})
    public String places(
            @RequestParam(name = "q", required = false, defaultValue = "") String q,
            Model model
    ) {
        List<PlaceResult> results = List.of();
        String error = null;

        try {
            if (!q.isBlank()) {
                results = scholarClient.searchPlaces(q);
            }
        } catch (IOException e) {
            error = e.getMessage();
        }

        model.addAttribute("q", q);
        model.addAttribute("results", results);
        model.addAttribute("error", error);
        return "places";
    }

}
