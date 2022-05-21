package dev.javiervs.coronavirustracker.services.impl;

import dev.javiervs.coronavirustracker.models.LocationStats;
import dev.javiervs.coronavirustracker.services.CoronaVirusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CoronaVirusServiceImpl implements CoronaVirusService {

    @Value("${coronavirus.data.url}")
    private String CORONAVIRUS_DATA_URL;

    private List<LocationStats> allStats = new ArrayList<>();

    @Override
    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @Override
    public int getTotalReportedCases() {
        return allStats.stream()
                .mapToInt(LocationStats::getLatestTotalCases)
                .sum();
    }

    @Override
    public int getTotalNewCases() {
        return allStats.stream()
                .mapToInt(LocationStats::getDiffFromPrevDay)
                .sum();
    }

    @Override
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void loadActualCoronaVirusData() throws IOException, InterruptedException {
        String coronavirusData = fetchVirusData();
        StringReader stringReader = new StringReader(coronavirusData);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        allStats = csvFormat.parse(stringReader).stream()
                .map(csvRecordToLocationStats)
                .toList();
    }

    private final Function<CSVRecord, LocationStats> csvRecordToLocationStats =
            record -> {
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int prevDayCases = record.size() >= 2
                        ? Integer.parseInt(record.get(record.size() - 2))
                        : 0;

                return LocationStats.builder()
                        .state(record.get("Province/State"))
                        .country(record.get("Country/Region"))
                        .latestTotalCases(latestCases)
                        .diffFromPrevDay(latestCases - prevDayCases)
                        .build();
            };

    private String fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CORONAVIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        return httpResponse.body();
    }
}
