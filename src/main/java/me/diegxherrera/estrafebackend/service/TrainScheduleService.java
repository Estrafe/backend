package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.TrainSchedule;
import me.diegxherrera.estrafebackend.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainScheduleService {

    private final TrainScheduleRepository trainScheduleRepository;

    public TrainScheduleService(TrainScheduleRepository trainScheduleRepository) {
        this.trainScheduleRepository = trainScheduleRepository;
    }

    // ✅ Get all train schedules
    public List<TrainSchedule> getAllSchedules() {
        return trainScheduleRepository.findAll();
    }

    // ✅ Get a specific train schedule by ID
    public Optional<TrainSchedule> getScheduleById(UUID id) {
        return trainScheduleRepository.findById(id);
    }

    // ✅ Find schedules by train ID
    public List<TrainSchedule> getSchedulesByTrain(UUID trainId) {
        return trainScheduleRepository.findByTrainId(trainId);
    }

    // ✅ Find schedules by route ID
    public List<TrainSchedule> getSchedulesByRoute(UUID routeId) {
        return trainScheduleRepository.findByRouteId(routeId);
    }

    // ✅ Admin-Only: Create a new train schedule
    @Transactional
    public TrainSchedule createSchedule(TrainSchedule schedule) {
        return trainScheduleRepository.save(schedule);
    }

    // ✅ Admin-Only: Update an existing train schedule
    @Transactional
    public Optional<TrainSchedule> updateSchedule(UUID id, TrainSchedule updatedSchedule) {
        return trainScheduleRepository.findById(id).map(existingSchedule -> {
            existingSchedule.setTrain(updatedSchedule.getTrain());
            existingSchedule.setDepartureTime(updatedSchedule.getDepartureTime());
            existingSchedule.setArrivalTime(updatedSchedule.getArrivalTime());
            existingSchedule.setServiceDays(updatedSchedule.getServiceDays());
            existingSchedule.setRoute(updatedSchedule.getRoute());

            // ❌ Remove the line referencing stopSchedules:
            // existingSchedule.setStopSchedules(updatedSchedule.getStopSchedules());

            return trainScheduleRepository.save(existingSchedule);
        });
    }

    // ✅ Admin-Only: Delete a train schedule
    @Transactional
    public boolean deleteSchedule(UUID id) {
        if (trainScheduleRepository.existsById(id)) {
            trainScheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TrainSchedule> findSchedules(UUID originStationId, UUID destinationStationId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();          // e.g., 2025-03-05T00:00
        LocalDateTime endOfDay = startOfDay.plusDays(1);           // e.g., 2025-03-06T00:00
        return trainScheduleRepository
                .findByRoute_OriginStation_IdAndRoute_DestinationStation_IdAndDepartureTimeBetween(
                        originStationId,
                        destinationStationId,
                        startOfDay,
                        endOfDay
                );
    }
}