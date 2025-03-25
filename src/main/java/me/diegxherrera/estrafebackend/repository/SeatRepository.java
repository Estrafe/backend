package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Coach;
import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.model.Train;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

    // ✅ Get all seats in a specific train
    @Query("SELECT s FROM Seat s WHERE s.coach.train = :train")
    List<Seat> findByTrain(@Param("train") Train train);

    // ✅ Get all available (unbooked) seats in a coach
    List<Seat> findByCoachAndBookedFalse(Coach coach);

    // ✅ Get all booked seats in a coach (used to prevent coach deletion)
    List<Seat> findByCoachAndBookedTrue(Coach coach);

    // ✅ Get all seats in a coach (useful for admin seat management)
    List<Seat> findByCoach(Coach coach);

    // ✅ Find the first available seat in a coach (for ticket assignment)
    @Query("SELECT s FROM Seat s WHERE s.coach.id = :coachId AND s.booked = false ORDER BY s.seatNumber ASC")
    List<Seat> findFirstAvailableSeatByCoachId(@Param("coachId") UUID coachId);

    // ✅ Count available seats in a coach (for frontend display)
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.coach = :coach AND s.booked = false")
    int countAvailableSeats(@Param("coach") Coach coach);

    // ✅ Find a seat by ID (ensures we validate before booking)
    Optional<Seat> findById(UUID id);
}