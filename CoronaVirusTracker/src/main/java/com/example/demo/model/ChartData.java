package com.example.demo.model;

import java.util.List;

public class ChartData {
    private List<LocationStates> states;
    private List<String> countries;
    private List<Integer> latestTotalDeaths;
    private List<Integer> differFromPrevDay;

    public ChartData(List<LocationStates> states, List<String> countries,
                     List<Integer> latestTotalDeaths, List<Integer> differFromPrevDay) {
        this.states = states;
        this.countries = countries;
        this.latestTotalDeaths = latestTotalDeaths;
        this.differFromPrevDay = differFromPrevDay;
    }

    // Getters and setters
    public List<LocationStates> getStates() {
        return states;
    }

    public void setStates(List<LocationStates> states) {
        this.states = states;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<Integer> getLatestTotalDeaths() {
        return latestTotalDeaths;
    }

    public void setLatestTotalDeaths(List<Integer> latestTotalDeaths) {
        this.latestTotalDeaths = latestTotalDeaths;
    }

    public List<Integer> getDifferFromPrevDay() {
        return differFromPrevDay;
    }

    public void setDifferFromPrevDay(List<Integer> differFromPrevDay) {
        this.differFromPrevDay = differFromPrevDay;
    }
}
