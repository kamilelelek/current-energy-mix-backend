package org.example.backend.service;

import org.example.backend.client.CarbonIntensityClient;
import org.example.backend.client.GenerationInterval;
import org.example.backend.dto.ChargingOptimalWindowDto;
import org.example.backend.dto.DailyMixDto;
import org.example.backend.mapper.DailyMixMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.example.backend.client.CarbonIntensityClient.FORMATTER;

@Service
public class EnergyMixService {
    private final CarbonIntensityClient carbonIntensityClient;
    private final DailyMixMapper dailyMixMapper;

    public EnergyMixService(CarbonIntensityClient carbonIntensityClient, DailyMixMapper dailyMixMapper) {
        this.carbonIntensityClient = carbonIntensityClient;
        this.dailyMixMapper = dailyMixMapper;
    }

    public List<DailyMixDto> getEnergyMix() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(2);
        List<GenerationInterval> intervals = intervalsWithinRange(from, to);

        Map<LocalDate, List<GenerationInterval>> byDate = intervals.stream()
                .collect(Collectors.groupingBy(this::extractDate, TreeMap::new, Collectors.toList()));

        return byDate.entrySet().stream()
                .map(e -> dailyMixMapper.toDailyMixDto(e.getKey(), e.getValue()))
                .toList();
    }

    public ChargingOptimalWindowDto getChargingOptimalWindow(int hours) {
        int intervalsPerHour = 2;
        int windowSize = hours * intervalsPerHour;
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(1);
        List<GenerationInterval> intervals = intervalsWithinRange(from, to);
        List<GenerationInterval> futureIntervals = intervals.stream()
                .filter(interval -> LocalDateTime.parse(interval.from(), FORMATTER).isAfter(LocalDateTime.now()))
                .toList();
        List<Double> cleanPercByInterval = futureIntervals.stream()
                .map(interval -> dailyMixMapper.cleanPercentage(interval.generationMix()))
                .toList();

        BestWindow best = findBestWindow(cleanPercByInterval, windowSize);

        LocalDateTime start = LocalDateTime.parse(futureIntervals.get(best.startIndex()).from(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(futureIntervals.get(best.startIndex() + windowSize - 1).to(), FORMATTER);
        return new ChargingOptimalWindowDto(start, end, best.averageCleanPercent());
    }

    private record BestWindow(int startIndex, double averageCleanPercent) {
    }

    private BestWindow findBestWindow(List<Double> cleanPerc, int windowSize) {
        int bestStart = 0;
        double bestAverage = -1;

        for (int i = 0; i <= cleanPerc.size() - windowSize; i++) {
            double average = windowAverage(cleanPerc, i, windowSize);
            if (average > bestAverage) {
                bestAverage = average;
                bestStart = i;
            }
        }
        return new BestWindow(bestStart, bestAverage);
    }

    private double windowAverage(List<Double> values, int start, int windowSize) {
        return values.subList(start, start + windowSize).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }

    private LocalDate extractDate(GenerationInterval interval) {
        return LocalDateTime.parse(interval.from(), FORMATTER).toLocalDate();
    }

    private List<GenerationInterval> intervalsWithinRange(LocalDate from, LocalDate to) {
        return carbonIntensityClient.getEnergyForNextDays(from, to).stream()
                .filter(interval -> {
                    LocalDate date = extractDate(interval);
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .toList();
    }
}
