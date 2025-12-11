package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.repository.BilRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/biler")
public class BilController {

    private final BilRepository bilRepository;

    public BilController(BilRepository bilRepository) {
        this.bilRepository = bilRepository;
    }

    @GetMapping
    public String bilerPage(Model model) {
        model.addAttribute("bil", new Bil());
        model.addAttribute("biler", bilRepository.findAll());
        return "biler";
    }

    @PostMapping
    public String createBil(@ModelAttribute("bil") Bil bil) {
        bilRepository.save(bil);
        return "redirect:/biler";
    }
}
