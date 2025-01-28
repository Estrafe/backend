package me.diegxherrera.estrafebackend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import me.diegxherrera.estrafebackend.model.City;
import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.repository.StationRepository;
import me.diegxherrera.estrafebackend.service.StationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class StationTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllStations() {
        City cityMock = new City("Madrid");
        Station station1 = new Station("Estación de Atocha - Almudena Grandes", cityMock);
        Station station2 = new Station("Estación de Chamartín - Clara Campoamor", cityMock);
        when(stationRepository.findAll()).thenReturn(List.of(station1, station2));

        List<Station> result = stationService.findAllStations();

        assertEquals(2, result.size());
        assertEquals("Estación de Atocha - Almudena Grandes", result.get(0).getName());
        assertEquals("Estación de Chamartín - Clara Campoamor", result.get(1).getName());
    }

    @Test
    public void testFindStationById() {
        UUID stationId = UUID.randomUUID();
        Station station = new Station("Estación de Príncipe Pío", new City("Madrid"));
        station.setId(stationId);

        Mockito.when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        Optional<Station> result = stationService.findStationById(stationId);

        // Ensure the result is not empty
        Assertions.assertTrue(result.isPresent());

        // Verify the station name
        Assertions.assertEquals("Estación de Príncipe Pío", result.get().getName());
    }
}