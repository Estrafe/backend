package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.City;
import me.diegxherrera.estrafebackend.model.Station;
import me.diegxherrera.estrafebackend.service.CityService;
import me.diegxherrera.estrafebackend.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/city")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    // Protect all endpoints with a specific role (e.g., "ROLE_ADMIN")
    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<City> findAllCities() {
        return cityService.findAllCities();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public Optional<City> getCityById(@PathVariable UUID id) {
        return cityService.findCityById(id);
    }

    @PostMapping
    public City createCity(@RequestBody City city) {
        return cityService.createCity(city);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable UUID id) {
        cityService.deleteCity(id);
    }
}