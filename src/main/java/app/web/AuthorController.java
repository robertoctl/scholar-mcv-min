package app.web;

import app.service.ScholarClient;
import app.model.Author;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Controller
public class AuthorController {
    private final ScholarClient scholar;

    public AuthorController(ScholarClient scholar) {
        this.scholar = scholar;
    }

    @GetMapping({"/", "/authors"})
    public String authors(@RequestParam(name = "query", required = false, defaultValue = "") String query,
                          Model model) {
        List<Author> results = Collections.emptyList();
        String error = null;
        try {
            if (!query.isBlank()) {
                results = scholar.searchAuthors(query);
            }
        } catch (IOException e) {
            error = e.getMessage();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("query", query);
        model.addAttribute("authors", results);
        model.addAttribute("error", error);
        return "authors";
    }
}
