package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    // Public: Get all stations (for users searching for stations)
    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = stationService.findAllStations();
        return ResponseEntity.ok(stations);
    }

    // Public: Get a specific station by ID
    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable UUID id) {
        return stationService.findStationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // Admin-Only: Create a new station
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station station) {
        Station createdStation = stationService.createStation(station);
        return ResponseEntity.ok(createdStation);
    }

    // Admin-Only: Update an existing station (full update)
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<Station> updateStation(@PathVariable UUID id, @RequestBody Station updatedStation) {
        return stationService.findStationById(id)
                .map(existingStation -> {
                    existingStation.setName(updatedStation.getName());
                    existingStation.setCity(updatedStation.getCity());
                    Station savedStation = stationService.createStation(existingStation);
                    return ResponseEntity.ok(savedStation);
                })
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // Admin-Only: Partially update a station
    @Secured("ROLE_ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<Station> patchStation(@PathVariable UUID id, @RequestBody Station partialUpdate) {
        return stationService.findStationById(id)
                .map(existingStation -> {
                    if (partialUpdate.getName() != null) {
                        existingStation.setName(partialUpdate.getName());
                    }
                    if (partialUpdate.getCity() != null) {
                        existingStation.setCity(partialUpdate.getCity());
                    }
                    Station savedStation = stationService.createStation(existingStation);
                    return ResponseEntity.ok(savedStation);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin-Only: Delete a station
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable UUID id) {
        if (stationService.findStationById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stationService.deleteStation(id);
        return ResponseEntity.ok().body(Map.of("message", "Station deleted successfully"));
    }
}