package org.example.backend.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.backend.client.ExternalApiException;
import org.example.backend.dto.ChargingOptimalWindowDto;
import org.example.backend.dto.DailyMixDto;
import org.example.backend.service.EnergyMixService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<DailyMixDto>> getEnergyMix() {
        return ResponseEntity.ok(energyMixService.getEnergyMix());
    }

    @GetMapping("/window-charging")
    public ResponseEntity<ChargingOptimalWindowDto> getChargingOptimalWindow(@RequestParam @Min(1) @Max(6) int hours) {
        return ResponseEntity.ok(energyMixService.getChargingOptimalWindow(hours));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiException(ExternalApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
    }
}
