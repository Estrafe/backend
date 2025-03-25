package me.diegxherrera.estrafebackend.controller.v1;

import lombok.RequiredArgsConstructor;
import me.diegxherrera.estrafebackend.model.Ticket;
import me.diegxherrera.estrafebackend.model.TrainSchedule;
import me.diegxherrera.estrafebackend.repository.TrainScheduleRepository;
import me.diegxherrera.estrafebackend.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TrainScheduleRepository trainScheduleRepository;

    /**
     * Fetch all tickets.
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    /**
     * Fetch a specific ticket by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ Fetch tickets for a specific train schedule with all necessary details.
     */
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Ticket>> getTicketsBySchedule(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(ticketService.getTicketsBySchedule(scheduleId));
    }


    /**
     * ✅ Fetch tickets booked by a user.
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable String username) {
        return ResponseEntity.ok(ticketService.getTicketsByUser(username));
    }


    @GetMapping("/schedules")
    public ResponseEntity<List<Map<String, Object>>> getAvailableTrainSchedules(
            @RequestParam("originStationId") UUID originStationId,
            @RequestParam("destinationStationId") UUID destinationStationId,
            @RequestParam("departureDate") String departureDateStr) {

        try {
            LocalDate departureDate = LocalDate.parse(departureDateStr);
            LocalDateTime startTime = departureDate.atStartOfDay();
            LocalDateTime endTime = departureDate.atTime(LocalTime.MAX);

            List<TrainSchedule> schedules = trainScheduleRepository.findByRoute_OriginStation_IdAndRoute_DestinationStation_IdAndDepartureTimeBetween(
                    originStationId, destinationStationId, startTime, endTime);

            if (schedules.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            // Convert TrainSchedule to a structured response
            List<Map<String, Object>> response = schedules.stream().map(schedule -> {
                Map<String, Object> scheduleData = new HashMap<>();
                scheduleData.put("id", schedule.getId());
                scheduleData.put("schedule", Map.of(
                        "id", schedule.getId(),
                        "departureTime", schedule.getDepartureTime(),
                        "arrivalTime", schedule.getArrivalTime(),
                        "basePrice", schedule.getBasePrice(),
                        "route", Map.of(
                                "id", schedule.getRoute().getId(),
                                "name", schedule.getRoute().getName(),
                                "originStation", Map.of(
                                        "id", schedule.getRoute().getOriginStation().getId(),
                                        "name", schedule.getRoute().getOriginStation().getName()
                                ),
                                "destinationStation", Map.of(
                                        "id", schedule.getRoute().getDestinationStation().getId(),
                                        "name", schedule.getRoute().getDestinationStation().getName()
                                )
                        )
                ));
                scheduleData.put("train", Map.of(
                        "id", schedule.getTrain().getId(),
                        "name", schedule.getTrain().getName(),
                        "accessible", schedule.getTrain().isAccessible(),
                        "animalsEnabled", schedule.getTrain().isAnimalsEnabled(),
                        "co2Compliant", schedule.getTrain().isCo2Compliant(),
                        "service", schedule.getTrain().getService()
                ));
                scheduleData.put("availableSeats", schedule.getTrain().getCoaches().stream()
                        .flatMap(coach -> coach.getSeats().stream())
                        .filter(seat -> !seat.isBooked())
                        .count()
                );
                return scheduleData;
            }).toList();


            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }


    /**
     * Book a ticket for a specific train schedule.
     */
    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(
            @RequestParam UUID scheduleId,
            @RequestParam UUID seatId,
            @RequestParam String username) {
        try {
            Ticket bookedTicket = ticketService.bookTicket(scheduleId, seatId, username);
            return ResponseEntity.ok(bookedTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    /**
     * Cancel a ticket.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTicket(@PathVariable UUID id) {
        boolean success = ticketService.cancelTicket(id);
        if (success) {
            return ResponseEntity.ok().body("{ \"message\": \"Ticket cancelled successfully.\" }");
        }
        return ResponseEntity.notFound().build();
    }
}