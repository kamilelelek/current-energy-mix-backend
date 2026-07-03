package org.example.backend.service;

import org.example.backend.client.CarbonIntensityClient;
import org.example.backend.client.GenerationInterval;
import org.example.backend.dto.ChargingOptimalWindowDto;
import org.example.backend.dto.DailyMixDto;
import org.example.backend.mapper.DailyMixMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnergyMixServiceTest {
    private final CarbonIntensityClient carbonIntensityClient = mock(CarbonIntensityClient.class);
    private final DailyMixMapper dailyMixMapper = mock(DailyMixMapper.class);
    private final EnergyMixService energyMixService = new EnergyMixService(carbonIntensityClient, dailyMixMapper);

    @Test
    void shouldPickWindowWithHighestCleanPercentage() {
        List<GenerationInterval> intervals = List.of(
                new GenerationInterval("2026-07-03T00:00Z", "2026-07-03T00:30Z", List.of()),
                new GenerationInterval("2026-07-03T00:30Z", "2026-07-03T01:00Z", List.of()),
                new GenerationInterval("2026-07-03T01:00Z", "2026-07-03T01:30Z", List.of()),
                new GenerationInterval("2026-07-03T01:30Z", "2026-07-03T02:00Z", List.of())
        );
        when(carbonIntensityClient.getEnergyForNextDays(any(), any())).thenReturn(intervals);
        when(dailyMixMapper.cleanPercentage(any())).thenReturn(10.0, 90.0, 90.0, 10.0);

        ChargingOptimalWindowDto result = energyMixService.getChargingOptimalWindow(1);

        assertEquals(LocalDateTime.parse("2026-07-03T00:30"), result.start());
        assertEquals(LocalDateTime.parse("2026-07-03T01:30"), result.end());
        assertEquals(90.0, result.percentageOfCleanEnergy());
    }

    @Test
    void shouldGroupIntervalsByDate() {
        List<GenerationInterval> intervals = List.of(
                new GenerationInterval("2026-07-04T23:30Z", "2026-07-05T00:00Z", List.of()),
                new GenerationInterval("2026-07-03T00:00Z", "2026-07-03T00:30Z", List.of())
        );
        when(carbonIntensityClient.getEnergyForNextDays(any(), any())).thenReturn(intervals);
        when(dailyMixMapper.toDailyMixDto(any(), any()))
                .thenAnswer(invocation -> new DailyMixDto(invocation.getArgument(0), List.of(), 0));

        List<DailyMixDto> result = energyMixService.getEnergyMix();

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2026, 7, 3), result.get(0).date());
        assertEquals(LocalDate.of(2026, 7, 4), result.get(1).date());
    }
}
