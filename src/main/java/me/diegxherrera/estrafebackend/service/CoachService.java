package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.Coach;
import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.model.Train;
import me.diegxherrera.estrafebackend.repository.CoachRepository;
import me.diegxherrera.estrafebackend.repository.SeatRepository;
import me.diegxherrera.estrafebackend.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CoachService {

    private final CoachRepository coachRepository;
    private final TrainRepository trainRepository;
    private final SeatRepository seatRepository;

    public CoachService(CoachRepository coachRepository, TrainRepository trainRepository, SeatRepository seatRepository) {
        this.coachRepository = coachRepository;
        this.trainRepository = trainRepository;
        this.seatRepository = seatRepository;
    }

    // ✅ Get all coaches assigned to a specific train
    public List<Coach> getCoachesByTrain(UUID trainId) {
        Optional<Train> train = trainRepository.findById(trainId);
        return train.map(coachRepository::findByTrain).orElseThrow(() -> new IllegalArgumentException("Train not found"));
    }

    // ✅ Get a specific coach by ID
    public Optional<Coach> getCoachById(UUID id) {
        return coachRepository.findById(id);
    }

    // ✅ Admin-Only: Create a new coach and assign it to a train
    @Transactional
    public Coach createCoach(UUID trainId, Coach coach) {
        Optional<Train> train = trainRepository.findById(trainId);
        if (train.isEmpty()) {
            throw new IllegalArgumentException("Train not found");
        }

        // Ensure coach number is unique within the train
        boolean exists = coachRepository.findByTrain(train.get())
                .stream()
                .anyMatch(c -> c.getCoachNumber() == coach.getCoachNumber());

        if (exists) {
            throw new IllegalArgumentException("Coach number must be unique within the train");
        }

        coach.setTrain(train.get());
        return coachRepository.save(coach);
    }

    // ✅ Admin-Only: Update a coach
    @Transactional
    public Optional<Coach> updateCoach(UUID id, Coach updatedCoach) {
        return coachRepository.findById(id).map(existingCoach -> {
            existingCoach.setCoachNumber(updatedCoach.getCoachNumber());
            existingCoach.setCoachType(updatedCoach.getCoachType());
            return coachRepository.save(existingCoach);
        });
    }

    // ✅ Admin-Only: Delete a coach (only if it has no booked seats)
    @Transactional
    public boolean deleteCoach(UUID id) {
        Optional<Coach> coach = coachRepository.findById(id);
        if (coach.isEmpty()) {
            return false;
        }

        // Prevent deletion if any seat is booked
        List<Seat> bookedSeats = seatRepository.findByCoachAndBookedTrue(coach.get());
        if (!bookedSeats.isEmpty()) {
            throw new IllegalStateException("Cannot delete coach with booked seats");
        }

        coachRepository.deleteById(id);
        return true;
    }

    // ✅ Get available seats in a coach (for ticket assignment)
    public List<Seat> getAvailableSeats(UUID coachId) {
        Optional<Coach> coach = coachRepository.findById(coachId);
        if (coach.isEmpty()) {
            throw new IllegalArgumentException("Coach not found");
        }

        return seatRepository.findByCoachAndBookedFalse(coach.get());
    }

    // ✅ Assign a coach & seat to a ticket (for ticket purchase)
    @Transactional
    public Seat assignSeat(UUID coachId) {
        Optional<Coach> coach = coachRepository.findById(coachId);
        if (coach.isEmpty()) {
            throw new IllegalArgumentException("Coach not found");
        }

        // Find the first available seat
        List<Seat> availableSeats = seatRepository.findByCoachAndBookedFalse(coach.get());
        if (availableSeats.isEmpty()) {
            throw new IllegalStateException("No available seats in this coach");
        }

        Seat seatToBook = availableSeats.get(0);
        seatToBook.bookSeat();
        return seatRepository.save(seatToBook);
    }
}