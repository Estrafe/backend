package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    // Custom query methods (if needed)
}