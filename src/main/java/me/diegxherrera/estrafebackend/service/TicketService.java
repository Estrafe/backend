package me.diegxherrera.estrafebackend.service;

import lombok.RequiredArgsConstructor;
import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.model.Ticket;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import me.diegxherrera.estrafebackend.repository.SeatRepository;
import me.diegxherrera.estrafebackend.repository.TicketRepository;
import me.diegxherrera.estrafebackend.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final SeatRepository seatRepository;

    /**
     * ✅ Fetch all tickets.
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * ✅ Fetch a ticket by ID.
     */
    public Optional<Ticket> getTicketById(UUID id) {
        return ticketRepository.findById(id);
    }

    /**
     * ✅ Fetch tickets for a specific train schedule with full train details.
     */
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsBySchedule(UUID scheduleId) {
        return ticketRepository.findByScheduleId(scheduleId);
    }

    /**
     * ✅ Fetch tickets booked by a user with full train details.
     */
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUser(String username) {
        return ticketRepository.findByUsername(username);
    }

    public List<TrainSchedule> getAvailableTrainSchedules(UUID originStationId, UUID destinationStationId, LocalDate departureDate) {
        return ticketRepository.findSchedulesWithAvailableSeats(originStationId, destinationStationId, departureDate);
    }

    /**
     * ✅ Book a ticket for a train schedule.
     */
    @Transactional
    public Ticket bookTicket(UUID scheduleId, UUID seatId, String username) {
        // 🔍 Step 1: Check if the schedule exists
        TrainSchedule schedule = trainScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Train Schedule ID"));

        // 🔍 Step 2: Check if the seat exists
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Seat ID"));

        // 🔍 Step 3: Check if the seat is already booked
        Optional<Ticket> existingTicket = ticketRepository.findByScheduleIdAndSeatId(scheduleId, seatId);
        if (existingTicket.isPresent()) {
            throw new IllegalArgumentException("Seat is already booked for this schedule");
        }

        // 🆕 Step 4: Create the new ticket
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setSchedule(schedule);
        ticket.setSeat(seat);
        ticket.setCoach(seat.getCoach()); // Assuming seat belongs to a coach
        ticket.setTrain(schedule.getTrain()); // Link to the train from the schedule
        ticket.setUsername(username);
        ticket.setStatus(Ticket.TicketStatus.BOOKED); // Mark ticket as booked

        // 💾 Step 5: Save the ticket
        return ticketRepository.save(ticket);
    }

    /**
     * ✅ Fetch available schedules for a given route and date.
     */
    public List<TrainSchedule> getSchedulesForRoute(UUID originStationId, UUID destinationStationId, LocalDate date) {
        // Define the start and end of the requested day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Fetch schedules matching the route within the given date range
        return trainScheduleRepository.findByRoute_OriginStation_IdAndRoute_DestinationStation_IdAndDepartureTimeBetween(
                originStationId, destinationStationId, startOfDay, endOfDay
        );
    }


    /**
     * ✅ Cancel a ticket.
     */
    @Transactional
    public boolean cancelTicket(UUID id) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(Ticket.TicketStatus.CANCELLED); // Update ticket status
            ticketRepository.save(ticket);
            return true;
        }
        return false;
    }
}