package org.example.backend.dto;

import org.example.backend.client.FuelPercentage;

import java.time.LocalDate;
import java.util.List;

public record DailyMixDto(
    LocalDate date,
    List<FuelPercentage> averageFuelMix,
    double percentage)
{}
