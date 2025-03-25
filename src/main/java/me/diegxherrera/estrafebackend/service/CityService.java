package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.City;
import me.diegxherrera.estrafebackend.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CityService {

    private final CityRepository cityRepository;

    @Autowired
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> findAllCities() {
        return cityRepository.findAll();
    }

    public Optional<City> findCityById(UUID id) {
        return cityRepository.findById(id);
    }

    public City createCity(City city) {
        if (city.getId() == null) {
            throw new IllegalArgumentException("City id is null.");
        }

        if (city.getName() == null) {
            throw new IllegalArgumentException("City name is null.");
        }

        return cityRepository.save(city);
    }

    public void deleteCity(UUID id) {
        cityRepository.deleteById(id);
    }
}