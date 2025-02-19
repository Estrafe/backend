package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.Coach;
import me.diegxherrera.estrafebackend.model.Seat;
import me.diegxherrera.estrafebackend.model.Train;
import me.diegxherrera.estrafebackend.service.CoachService;
import me.diegxherrera.estrafebackend.service.SeatService;
import me.diegxherrera.estrafebackend.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    private final CoachService coachService;
    private final TrainService trainService;
    private final SeatService seatService;

    public CoachController(CoachService coachService, TrainService trainService, SeatService seatService) {
        this.coachService = coachService;
        this.trainService = trainService;
        this.seatService = seatService;
    }

    // ✅ Public: Get all coaches of a train
    @GetMapping("/train/{trainId}")
    public ResponseEntity<List<Coach>> getCoachesByTrain(@PathVariable UUID trainId) {
        Optional<Train> train = trainService.getTrainById(trainId);
        return train.map(value -> ResponseEntity.ok(coachService.getCoachesByTrain(trainId)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Public: Get available seats in a coach
    @GetMapping("/{coachId}/seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable UUID coachId) {
        Optional<Coach> coach = coachService.getCoachById(coachId);
        return coach.map(value -> ResponseEntity.ok(seatService.getAvailableSeats(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Create a new coach and assign it to a train
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        return ResponseEntity.ok(coachService.createCoach(coach.getTrain().getId(), coach));
    }

    // 🔒 Admin-Only: Update an existing coach
    @Secured("ROLE_ADMIN")
    @PutMapping("/{coachId}")
    public ResponseEntity<Coach> updateCoach(@PathVariable UUID coachId, @RequestBody Coach updatedCoach) {
        Optional<Coach> coach = coachService.updateCoach(coachId, updatedCoach);
        return coach.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Delete a coach
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{coachId}")
    public ResponseEntity<Void> deleteCoach(@PathVariable UUID coachId) {
        boolean deleted = coachService.deleteCoach(coachId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}