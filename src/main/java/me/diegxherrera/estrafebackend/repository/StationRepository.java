package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.City;
import me.diegxherrera.estrafebackend.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {
    List<Station> findByCity(City city);
    // Custom query methods (if needed)

    @Query("SELECT s FROM Station s ORDER BY s.name ASC")
    List<Station> findAllOrderedByName();

}