package org.example.backend.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FuelPercentage (
    String fuel,
    @JsonProperty("perc") double percentage){
}
