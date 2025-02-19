package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.StopSchedule;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import me.diegxherrera.estrafebackend.repository.StopScheduleRepository;
import me.diegxherrera.estrafebackend.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StopScheduleService {

    private final StopScheduleRepository stopScheduleRepository;
    private final TrainScheduleRepository trainScheduleRepository;

    public StopScheduleService(StopScheduleRepository stopScheduleRepository, TrainScheduleRepository trainScheduleRepository) {
        this.stopScheduleRepository = stopScheduleRepository;
        this.trainScheduleRepository = trainScheduleRepository;
    }

    // ✅ Get all stop schedules for a specific train schedule
    public List<StopSchedule> getStopsBySchedule(UUID scheduleId) {
        Optional<TrainSchedule> schedule = trainScheduleRepository.findById(scheduleId);
        return schedule.map(stopScheduleRepository::findByTrainSchedule)
                .orElseThrow(() -> new IllegalArgumentException("Train Schedule not found"));
    }

    // ✅ Get a specific stop schedule by ID
    public Optional<StopSchedule> getStopById(UUID id) {
        return stopScheduleRepository.findById(id);
    }

    // ✅ Admin-Only: Create a new stop schedule
    @Transactional
    public StopSchedule createStopSchedule(UUID scheduleId, StopSchedule stop) {
        Optional<TrainSchedule> schedule = trainScheduleRepository.findById(scheduleId);
        if (schedule.isEmpty()) {
            throw new IllegalArgumentException("Train Schedule not found");
        }

        stop.setTrainSchedule(schedule.get());
        return stopScheduleRepository.save(stop);
    }

    // ✅ Admin-Only: Update an existing stop schedule
    @Transactional
    public Optional<StopSchedule> updateStopSchedule(UUID id, StopSchedule updatedStop) {
        return stopScheduleRepository.findById(id).map(existingStop -> {
            existingStop.setStopName(updatedStop.getStopName());
            existingStop.setScheduledArrival(updatedStop.getScheduledArrival());
            existingStop.setScheduledDeparture(updatedStop.getScheduledDeparture());
            existingStop.setActualArrival(updatedStop.getActualArrival());
            existingStop.setActualDeparture(updatedStop.getActualDeparture());
            return stopScheduleRepository.save(existingStop);
        });
    }

    // ✅ Admin-Only: Delete a stop schedule
    @Transactional
    public boolean deleteStopSchedule(UUID id) {
        if (stopScheduleRepository.existsById(id)) {
            stopScheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}