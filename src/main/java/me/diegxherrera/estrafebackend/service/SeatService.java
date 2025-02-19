package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.Coach;
import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    // ✅ Get all available (unbooked) seats in a coach
    public List<Seat> getAvailableSeats(Coach coach) {
        return seatRepository.findByCoachAndBookedFalse(coach);
    }

    // ✅ Get all booked seats in a coach (used to prevent coach deletion)
    public List<Seat> getBookedSeats(Coach coach) {
        return seatRepository.findByCoachAndBookedTrue(coach);
    }

    // ✅ Get all seats in a coach (useful for admin seat management)
    public List<Seat> getAllSeatsByCoach(Coach coach) {
        return seatRepository.findByCoach(coach);
    }

    // ✅ Assign the first available seat in a coach (for ticket purchase)
    @Transactional
    public Optional<Seat> assignSeat(UUID coachId) {
        Optional<Seat> availableSeat = seatRepository.findFirstAvailableSeatByCoachId(coachId).stream().findFirst();
        if (availableSeat.isPresent()) {
            Seat seat = availableSeat.get();
            seat.setBooked(true);
            return Optional.of(seatRepository.save(seat));
        }
        return Optional.empty();
    }

    // ✅ Unbook a seat (for ticket cancellation)
    @Transactional
    public Optional<Seat> unbookSeat(UUID seatId) {
        Optional<Seat> seat = seatRepository.findById(seatId);
        if (seat.isPresent() && seat.get().isBooked()) {
            seat.get().setBooked(false);
            return Optional.of(seatRepository.save(seat.get()));
        }
        return Optional.empty();
    }

    // ✅ Count available seats in a coach (for UI display)
    public int countAvailableSeats(Coach coach) {
        return seatRepository.countAvailableSeats(coach);
    }
}