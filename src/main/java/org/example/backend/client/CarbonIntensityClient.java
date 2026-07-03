package org.example.backend.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CarbonIntensityClient {

    private static final String BASE_URL = "https://api.carbonintensity.org.uk";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

    private final RestClient restClient;

    public CarbonIntensityClient() {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public List<GenerationInterval> getEnergyForNextDays(LocalDate from, LocalDate to) {
        String fromParam = from.atStartOfDay().format(FORMATTER);
        String toParam = to.atTime(23, 30).format(FORMATTER);
        CarbonIntensityResponse response;
        try {
            response = restClient.get()
                    .uri("/generation/" + fromParam + "/" + toParam)
                    .retrieve()
                    .body(CarbonIntensityResponse.class);
        } catch (RestClientException e) {
            throw new ExternalApiException("Nie udało się pobrać danych z Carbon Intensity API", e);
        }

        if (response == null || response.data() == null) {
            throw new ExternalApiException("Carbon Intensity API zwróciło pustą odpowiedź");
        }
        return response.data();
    }

}
