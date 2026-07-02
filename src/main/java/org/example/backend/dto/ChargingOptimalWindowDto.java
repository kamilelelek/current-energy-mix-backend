package org.example.backend.dto;

import java.time.LocalDateTime;

public record ChargingOptimalWindowDto(LocalDateTime start, LocalDateTime end, double percentageOfCleanEnergy) {
}
