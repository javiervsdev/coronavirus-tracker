package dev.javiervs.coronavirustracker.services;

import java.io.IOException;

public interface CoronaVirusService {
    void fetchVirusData() throws IOException, InterruptedException;
}
