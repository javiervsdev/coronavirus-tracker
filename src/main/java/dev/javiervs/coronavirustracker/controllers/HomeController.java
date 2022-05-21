package dev.javiervs.coronavirustracker.controllers;

import dev.javiervs.coronavirustracker.services.CoronaVirusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CoronaVirusService coronaVirusService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("locationStats", coronaVirusService.getAllStats());
        model.addAttribute("totalReportedCases", coronaVirusService.getTotalReportedCases());
        model.addAttribute("totalNewCases", coronaVirusService.getTotalNewCases());
        return "home";
    }
}
