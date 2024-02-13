package com.example.demo.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.model.LocationStates;

@Service
public class CoronaVirusDataServices {

    private List<LocationStates> allStates = new ArrayList<>();

    public List<LocationStates> getAllStates() {
        return allStates;
    }

    // Method to retrieve countries
    public List<String> getAllCountries() {
        return allStates.stream()
                        .map(LocationStates::getCountry)
                        .distinct()
                        .collect(Collectors.toList());
    }

    // Method to retrieve death counts by country
    public List<Integer> getDeathCountsByCountry() {
        return allStates.stream()
                        .collect(Collectors.groupingBy(LocationStates::getCountry, Collectors.summingInt(LocationStates::getLatestTotalDeaths)))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
    }

    private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";

    @PostConstruct
    @Scheduled(cron = "0 0 * * * *") // Fetch data every hour
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        List<LocationStates> newStates = new ArrayList<>();
        for (CSVRecord record : records) {
            LocationStates state = new LocationStates();
            state.setState(record.get("Province/State"));
            state.setCountry(record.get("Country/Region"));
            int latestCase = Integer.parseInt(record.get(record.size() - 1));
            int prevCase = Integer.parseInt(record.get(record.size() - 2));
            state.setLatestTotalDeaths(latestCase);
            state.setDifferFromPrevay(latestCase - prevCase);
            newStates.add(state);
        }
        this.allStates = newStates;
    }

    // Method to generate country IDs from 1 to 270
    public List<String> getCountryIds() {
        return IntStream.rangeClosed(1, 270)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.toList());
    }

    // Method to retrieve states by country
    public List<LocationStates> getStatesByCountry(String countryName) {
        return allStates.stream()
                        .filter(state -> state.getCountry().equalsIgnoreCase(countryName))
                        .collect(Collectors.toList());
    }

    // Method to retrieve latest total deaths by country
    public List<Integer> getLatestTotalDeaths(String countryName) {
        return allStates.stream()
                        .filter(state -> state.getCountry().equalsIgnoreCase(countryName))
                        .map(LocationStates::getLatestTotalDeaths)
                        .collect(Collectors.toList());
    }

    // Method to retrieve difference from previous day by country
    public List<Integer> getDifferFromPrevay(String countryName) {
        return allStates.stream()
                        .filter(state -> state.getCountry().equalsIgnoreCase(countryName))
                        .map(LocationStates::getDifferFromPrevay)
                        .collect(Collectors.toList());
    }
}
