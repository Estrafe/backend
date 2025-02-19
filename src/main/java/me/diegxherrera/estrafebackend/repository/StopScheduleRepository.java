package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.StopSchedule;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StopScheduleRepository extends JpaRepository<StopSchedule, UUID> {

    // ✅ Find all stop schedules for a specific train schedule
    List<StopSchedule> findByTrainSchedule(TrainSchedule trainSchedule);
}