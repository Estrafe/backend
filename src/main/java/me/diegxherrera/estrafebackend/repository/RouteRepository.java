package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {

    // ✅ Find routes by origin and destination
    List<Route> findByOriginStationIdAndDestinationStationId(UUID originStationId, UUID destinationStationId);
}