package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.Route;
import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    // ✅ Get all routes
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    // ✅ Get a specific route by ID
    public Optional<Route> getRouteById(UUID id) {
        return routeRepository.findById(id);
    }

    // ✅ Find routes by origin and destination
    public List<Route> findRoutesByOriginAndDestination(UUID originStationId, UUID destinationStationId) {
        return routeRepository.findByOriginStationIdAndDestinationStationId(originStationId, destinationStationId);
    }

    // ✅ Get all stations in a route
    public Optional<Set<Station>> getStationsInRoute(UUID id) {
        Optional<Route> route = routeRepository.findById(id);
        return route.map(Route::getStations);
    }

    // ✅ Admin-Only: Create a new route
    @Transactional
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    // ✅ Admin-Only: Update an existing route
    @Transactional
    public Optional<Route> updateRoute(UUID id, Route updatedRoute) {
        return routeRepository.findById(id).map(existingRoute -> {
            existingRoute.setName(updatedRoute.getName());
            existingRoute.setOriginStation(updatedRoute.getOriginStation());
            existingRoute.setDestinationStation(updatedRoute.getDestinationStation());
            existingRoute.setStations(updatedRoute.getStations());
            return routeRepository.save(existingRoute);
        });
    }

    // ✅ Admin-Only: Delete a route
    @Transactional
    public boolean deleteRoute(UUID id) {
        if (routeRepository.existsById(id)) {
            routeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}