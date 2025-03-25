package me.diegxherrera.estrafebackend.controller.v1;

import me.diegxherrera.estrafebackend.model.City;
import me.diegxherrera.estrafebackend.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/city")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<City> findAllCities() {
        return cityService.findAllCities();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public Optional<City> getCityById(@PathVariable UUID id) {
        return cityService.findCityById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public City createCity(@RequestBody City city) {
        return cityService.createCity(city);
    }

    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable UUID id) {
        cityService.deleteCity(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<City> patchCity(@PathVariable UUID id, @RequestBody City partialUpdate) {
        return cityService.findCityById(id)
                .map(existingCity -> {
                    if (partialUpdate.getName() != null) {
                        existingCity.setName(partialUpdate.getName());
                    }
                    City updatedCity = cityService.createCity(existingCity);
                    return ResponseEntity.ok(updatedCity);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}