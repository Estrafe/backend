package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, UUID> {

    // ✅ Find schedules by train ID
    List<TrainSchedule> findByTrainId(UUID trainId);

    // ✅ Find schedules by route ID
    List<TrainSchedule> findByRouteId(UUID routeId);
}