package dev.javiervs.coronavirustracker.services;

import dev.javiervs.coronavirustracker.models.LocationStats;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.List;

public interface CoronaVirusService {
    List<LocationStats> getAllStats();

    @PostConstruct
    void loadActualCoronaVirusData() throws IOException, InterruptedException;
}
