package org.example.backend.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GenerationInterval (
    String from,
    String to,
    @JsonProperty("generationmix") List<FuelPercentage> generationMix){
}
