package org.example.backend.client;

import java.util.List;

public record CarbonIntensityResponse (
   List<GenerationInterval> data){
}
