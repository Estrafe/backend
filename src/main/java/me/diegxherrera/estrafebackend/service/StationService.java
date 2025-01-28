package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StationService {

    private final StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
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

    public void deleteStation(UUID id) {
        stationRepository.deleteById(id);
    }
}