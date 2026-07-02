package org.example.backend.mapper;

import org.example.backend.client.FuelPercentage;
import org.example.backend.client.GenerationInterval;
import org.example.backend.dto.DailyMixDto;
import org.example.backend.model.CleanEnergySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailyMixMapper {

    public DailyMixDto toDailyMixDto(LocalDate date, List<GenerationInterval> intervals) {
        List<FuelPercentage> avgFuelMix = intervals.stream()
                .flatMap(interval -> interval.generationMix().stream())
                .collect(Collectors.groupingBy(FuelPercentage::fuel, Collectors.averagingDouble(FuelPercentage::percentage)))
                .entrySet().stream()
                .map(entry -> new FuelPercentage(entry.getKey(), entry.getValue()))
                .toList();

        return new DailyMixDto(date, avgFuelMix, cleanPercentage(avgFuelMix));
    }

    public double cleanPercentage(List<FuelPercentage> fuelPercentages) {
        return fuelPercentages.stream()
                .filter(f -> CleanEnergySource.isClean(f.fuel()))
                .mapToDouble(FuelPercentage::percentage)
                .sum();
    }
}
