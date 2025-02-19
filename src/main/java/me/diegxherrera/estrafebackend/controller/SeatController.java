package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.Coach;
import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    // ✅ Get available seats in a coach
    @GetMapping("/available/{coachId}")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable UUID coachId) {
        Coach coach = new Coach(); // ⚠️ Get this properly from your DB
        coach.setId(coachId);
        return ResponseEntity.ok(seatService.getAvailableSeats(coach));
    }

    // ✅ Assign a seat
    @PostMapping("/assign/{coachId}")
    public ResponseEntity<Seat> assignSeat(@PathVariable UUID coachId) {
        Optional<Seat> assignedSeat = seatService.assignSeat(coachId);
        return assignedSeat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Unbook a seat
    @PostMapping("/unbook/{seatId}")
    public ResponseEntity<Seat> unbookSeat(@PathVariable UUID seatId) {
        Optional<Seat> unbookedSeat = seatService.unbookSeat(seatId);
        return unbookedSeat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}