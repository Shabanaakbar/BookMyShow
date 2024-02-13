package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.LocationStates;
import com.example.demo.services.CoronaVirusDataServices;

@Controller
public class HomeController {

    private final CoronaVirusDataServices crnService;
    
    public HomeController(CoronaVirusDataServices crnService) {
        this.crnService = crnService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStates> allstates = crnService.getAllStates();
        int totalDeathsReported = allstates.stream().mapToInt(stat -> stat.getLatestTotalDeaths()).sum();
        
        // Pass the necessary data for the chart to the view
        List<String> countries = crnService.getAllCountries();
        List<Integer> deathCounts = crnService.getDeathCountsByCountry();
        
        model.addAttribute("LocationStates", allstates);
        model.addAttribute("totalDeathsReported", totalDeathsReported);
        model.addAttribute("countries", countries); // Add countries names to the model
        model.addAttribute("deathCounts", deathCounts);
        
        return "home";
    }
    
    @GetMapping("/viewChart")
    public String viewChart(Model model) {
        List<String> countries = crnService.getAllCountries(); // Get the list of countries names
        model.addAttribute("countries", countries); // Add countries names to the model
        return "ViewChart"; // Assuming ViewChart.html is in src/main/resources/templates
    }
}
