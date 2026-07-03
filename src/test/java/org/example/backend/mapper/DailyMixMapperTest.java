package org.example.backend.mapper;

import org.example.backend.client.FuelPercentage;
import org.example.backend.client.GenerationInterval;
import org.example.backend.dto.DailyMixDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class DailyMixMapperTest {
    private final DailyMixMapper dailyMixMapper = new DailyMixMapper();

    @Test
    void shouldReturnHundredPercent() {
        List<FuelPercentage> cleanFuel = List.of(
                new FuelPercentage("wind", 30),
                new FuelPercentage("solar", 30),
                new FuelPercentage("nuclear", 10),
                new FuelPercentage("hydro", 10),
                new FuelPercentage("biomass", 20)
        );
        double result = dailyMixMapper.cleanPercentage(cleanFuel);
        Assertions.assertEquals(100, result);
    }//pomijac duze/male litery

    @Test
    void shouldReturnZeroPercent() {
        List<FuelPercentage> dirtyFuel = List.of(
                new FuelPercentage("coal", 40),
                new FuelPercentage("gas", 30),
                new FuelPercentage("imports", 30)
        );
        double result = dailyMixMapper.cleanPercentage(dirtyFuel);
        Assertions.assertEquals(0, result);
    }

    @Test
    void shouldReturnOnlyCleanFuel() {
        List<FuelPercentage> mixedFuel = List.of(
                new FuelPercentage("gas", 37.5),
                new FuelPercentage("coal", 5.34),
                new FuelPercentage("wind", 33.25),
                new FuelPercentage("solar", 23.91)
        );
        double result = dailyMixMapper.cleanPercentage(mixedFuel);
        Assertions.assertEquals(57.16, result);
    }

    @Test
    void shouldReturnZeroEmptyList() {
        List<FuelPercentage> emptyList = List.of();
        double result = dailyMixMapper.cleanPercentage(emptyList);
        Assertions.assertEquals(0, result);
    }
    @Test
    void shouldMapOneInterval(){
        List<FuelPercentage> fuels= List.of(
                new FuelPercentage("wind",40),
                new FuelPercentage("gas",40),
                new FuelPercentage("solar",20)
        );
        GenerationInterval interval= new GenerationInterval(
                "2026-07-03T00:00Z",
                "2026-07-03T00:30Z",
                fuels
        );
        LocalDate dateTime= LocalDate.of(2026,7,3);
        DailyMixDto result= dailyMixMapper.toDailyMixDto(dateTime,List.of(interval));
        Assertions.assertEquals(dateTime,result.date());
        Assertions.assertEquals(3,result.averageFuelMix().size());
        Assertions.assertEquals(60, result.percentage());
    }
}