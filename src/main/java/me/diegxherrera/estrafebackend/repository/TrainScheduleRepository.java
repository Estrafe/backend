package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, UUID> {

    // ✅ Corrected method to fetch schedules within a date range
    List<TrainSchedule> findByRoute_OriginStation_IdAndRoute_DestinationStation_IdAndDepartureTimeBetween(
            UUID originStationId,
            UUID destinationStationId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // ✅ Existing useful methods
    List<TrainSchedule> findByTrainId(UUID trainId);
    List<TrainSchedule> findByRouteId(UUID routeId);
}