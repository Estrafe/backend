package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.Route;
import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // ✅ Public: Get all routes
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // ✅ Public: Get a specific route by ID
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable UUID id) {
        Optional<Route> route = routeService.getRouteById(id);
        return route.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Public: Search routes by origin and destination
    @GetMapping("/search")
    public ResponseEntity<List<Route>> searchRoutes(
            @RequestParam UUID originStationId,
            @RequestParam UUID destinationStationId) {
        List<Route> routes = routeService.findRoutesByOriginAndDestination(originStationId, destinationStationId);
        return ResponseEntity.ok(routes);
    }

    // ✅ Public: Get all stations in a route
    @GetMapping("/{id}/stations")
    public ResponseEntity<Set<Station>> getStationsInRoute(@PathVariable UUID id) {
        Optional<Set<Station>> stations = routeService.getStationsInRoute(id);
        return stations.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Create a new route
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        return ResponseEntity.ok(routeService.createRoute(route));
    }

    // 🔒 Admin-Only: Update an existing route
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable UUID id, @RequestBody Route updatedRoute) {
        Optional<Route> route = routeService.updateRoute(id, updatedRoute);
        return route.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Delete a route
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable UUID id) {
        boolean deleted = routeService.deleteRoute(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}