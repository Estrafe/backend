package me.diegxherrera.estrafebackend.service;

import jakarta.transaction.Transactional;
import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.repository.RouteRepository;
import me.diegxherrera.estrafebackend.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public StationService(StationRepository stationRepository, RouteRepository routeRepository) {
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
    }

    public List<Station> findAllStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> findStationById(UUID id) {
        return stationRepository.findById(id);
    }

    public Station createStation(Station station) {
        return stationRepository.save(station);
    }

    @Transactional
    public void deleteStation(UUID id) {
        Optional<Station> optionalStation = stationRepository.findById(id);
        if (optionalStation.isPresent()) {
            Station station = optionalStation.get();

            // 🔥 Step 1: Remove station from all associated routes
            station.getRoutes().forEach(route -> {
                route.getStations().remove(station);
                routeRepository.save(route); // Save changes
            });

            // 🔥 Step 2: Remove references from the station entity
            station.getRoutes().clear();
            stationRepository.save(station); // Ensure references are cleared

            // 🔥 Step 3: Delete the station
            stationRepository.delete(station);
        } else {
            throw new RuntimeException("Station not found");
        }
    }

}