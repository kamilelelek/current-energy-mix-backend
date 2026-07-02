package org.example.backend.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CleanEnergySource {
    BIOMASS("biomass"),
    NUCLEAR("nuclear"),
    HYDRO("hydro"),
    WIND("wind"),
    SOLAR("solar");

    private final String fuelName;

    CleanEnergySource(String fuelName) {
        this.fuelName = fuelName;
    }

    public String getFuelName() {
        return fuelName;
    }

    private static final Set<String> FUEL_NAMES = Arrays.stream(values())
            .map(CleanEnergySource::getFuelName)
            .collect(Collectors.toSet());

    public static boolean isClean(String fuel) {
        return FUEL_NAMES.contains(fuel);
    }
}
