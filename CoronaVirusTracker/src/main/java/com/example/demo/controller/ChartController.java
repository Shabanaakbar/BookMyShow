package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.ChartData;
import com.example.demo.model.LocationStates;
import com.example.demo.services.CoronaVirusDataServices;

@Controller
public class ChartController {

    @Autowired
    private CoronaVirusDataServices crnService;

    @GetMapping("/collectChartData")
    @ResponseBody
    public ChartData collectChartData() {
        try {
            List<String> countries = crnService.getAllCountries();
            List<Integer> deathCounts = crnService.getDeathCountsByCountry();
            List<LocationStates> states = crnService.getAllStates();
            List<Integer> latestTotalDeaths = crnService.getLatestTotalDeaths(null); // Pass null for all countries
            List<Integer> differFromPrevDay = crnService.getDifferFromPrevay(null); // Pass null for all countries

            return new ChartData(states, countries, latestTotalDeaths, differFromPrevDay);
        } catch (Exception e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null; // Or return an error response
        }
    }
}
