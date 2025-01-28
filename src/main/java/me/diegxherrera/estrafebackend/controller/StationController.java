package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    // Protect all endpoints with a specific role (e.g., "ROLE_ADMIN")
    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<Station> getAllStations() {
        return stationService.findAllStations();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public Optional<Station> getStationById(@PathVariable UUID id) {
        return stationService.findStationById(id);
    }

    @PostMapping
    public Station createStation(@RequestBody Station station) {
        return stationService.createStation(station);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void deleteStation(@PathVariable UUID id) {
        stationService.deleteStation(id);
    }
}