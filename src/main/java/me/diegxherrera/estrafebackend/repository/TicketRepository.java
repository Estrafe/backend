package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.model.Ticket;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // ✅ Load schedule + train in a single query to avoid lazy-loading issues
    @EntityGraph(attributePaths = {"schedule", "schedule.train", "schedule.route", "seat", "coach"})
    List<Ticket> findByScheduleId(UUID scheduleId);

    @Query("SELECT t.schedule FROM Ticket t WHERE t.status = 'AVAILABLE' AND t.schedule.route.originStation.id = :originStationId AND t.schedule.route.destinationStation.id = :destinationStationId AND DATE(t.schedule.departureTime) = :departureDate GROUP BY t.schedule")
    List<TrainSchedule> findSchedulesWithAvailableSeats(@Param("originStationId") UUID originStationId, @Param("destinationStationId") UUID destinationStationId, @Param("departureDate") LocalDate departureDate);

    // ✅ Find tickets booked by a user
    List<Ticket> findByUsername(String username);

    // ✅ Check if a seat is already booked on a specific train schedule
    Optional<Ticket> findByScheduleIdAndSeatId(UUID scheduleId, UUID seatId);

    boolean existsByScheduleAndSeat(TrainSchedule schedule, Seat seat);
}