package me.diegxherrera.estrafebackend.repository;

import jakarta.transaction.Transactional;
import me.diegxherrera.estrafebackend.model.City;
import me.diegxherrera.estrafebackend.model.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class StationTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private CityRepository cityRepository;

    private City city;

    @BeforeEach
    public void setUp() {
        if (cityRepository.count() == 0 && stationRepository.count() == 0) {
            // Create and save a city
            city = new City("Madrid");
            cityRepository.save(city);

            // Create and save stations for the city
            Station station1 = new Station("Estación de Atocha", city);
            Station station2 = new Station("Estación de Chamartín", city);
            stationRepository.save(station1);
            stationRepository.save(station2);
        } else {
            // If data already exists, fetch the existing city for reference
            city = cityRepository.findAll().get(0);
        }
    }

    @Test
    @Rollback(false)
    public void testFindStationById() {
        // Retrieve stations with consistent ordering by name
        List<Station> stations = stationRepository.findAllOrderedByName();

        // The first station should now always be "Estación de Atocha"
        UUID stationId = stations.get(0).getId();

        // Test the retrieval by ID
        Optional<Station> result = stationRepository.findById(stationId);
        Assertions.assertTrue(result.isPresent(), "Station should be found");
        Assertions.assertEquals("Estación de Atocha", result.get().getName(), "Station name should match");
    }

    @Test
    public void testFindStationsByCity() {
        // Retrieve stations by city
        List<Station> stations = stationRepository.findByCity(city);

        // Verify results
        Assertions.assertEquals(2, stations.size(), "City should have 2 stations");
        Assertions.assertTrue(stations.stream().anyMatch(s -> s.getName().equals("Estación de Chamartín")), "Atocha should exist");
        Assertions.assertTrue(stations.stream().anyMatch(s -> s.getName().equals("Estación de Atocha")), "Chamartín should exist");
    }

    @Test
    public void testDeleteStation() {
        // Delete a station
        List<Station> stations = stationRepository.findAll();
        stationRepository.delete(stations.get(0));

        // Verify the deletion
        List<Station> remainingStations = stationRepository.findAll();
        Assertions.assertEquals(1, remainingStations.size(), "Only 1 station should remain");
    }
}