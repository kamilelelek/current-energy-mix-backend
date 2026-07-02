package org.example.backend.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.backend.dto.ChargingOptimalWindowDto;
import org.example.backend.dto.DailyMixDto;
import org.example.backend.service.EnergyMixService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/energy-mix")
@CrossOrigin(origins = "*")
@Validated
public class EnergyMixController {
    private final EnergyMixService energyMixService;

    public EnergyMixController(EnergyMixService energyMixService) {
        this.energyMixService = energyMixService;
    }

    @GetMapping
    public List<DailyMixDto> getEnergyMix() {
        return energyMixService.getEnergyMix();
    }

    @GetMapping("/window-charging")
    public ChargingOptimalWindowDto getChargingOptimalWindow(@RequestParam @Min(1) @Max(6) int hours) {
        return energyMixService.getChargingOptimalWindow(hours);
    }
}
