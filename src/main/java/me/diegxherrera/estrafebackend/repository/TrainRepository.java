package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainRepository extends JpaRepository<Train, UUID> {

    @Query("SELECT t FROM Train t " +
            "JOIN t.route r " +
            "WHERE r.originStation.id = :departureStationId " +
            "AND r.destinationStation.id = :arrivalStationId " +
            "AND FUNCTION('DATE', t.departureTime) = :departureDate")
    List<Train> findTrains(
            @Param("departureStationId") UUID departureStationId,
            @Param("arrivalStationId") UUID arrivalStationId,
            @Param("departureDate") LocalDate departureDate
    );
}