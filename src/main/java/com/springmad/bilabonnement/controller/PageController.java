package com.springmad.bilabonnement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Denne controller styrer de simple statiske sider i applikationen,
    // som ikke kræver databasekald eller datahåndtering.

    @GetMapping("/")
    // Returnerer forsiden (index.html), når brugeren går til root URL'en.
    public String indexPage() {
        return "index";
    }

    @GetMapping("/about")
    // Returnerer about-siden (about.html), en informativ underside.
    public String aboutPage() {
        return "about";
    }
}
