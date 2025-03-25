package me.diegxherrera.estrafebackend.controller.v1;

import me.diegxherrera.estrafebackend.model.TrainSchedule;
import me.diegxherrera.estrafebackend.service.TrainScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class TrainScheduleController {

    private final TrainScheduleService trainScheduleService;

    public TrainScheduleController(TrainScheduleService trainScheduleService) {
        this.trainScheduleService = trainScheduleService;
    }

    // ✅ Public: Get all train schedules
    @GetMapping
    public ResponseEntity<List<TrainSchedule>> getAllSchedules() {
        return ResponseEntity.ok(trainScheduleService.getAllSchedules());
    }

    // ✅ Public: Get a specific train schedule by ID
    @GetMapping("/{id}")
    public ResponseEntity<TrainSchedule> getScheduleById(@PathVariable UUID id) {
        Optional<TrainSchedule> schedule = trainScheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Public: Get schedules for a specific train
    @GetMapping("/train/{trainId}")
    public ResponseEntity<List<TrainSchedule>> getSchedulesByTrain(@PathVariable UUID trainId) {
        return ResponseEntity.ok(trainScheduleService.getSchedulesByTrain(trainId));
    }

    // ✅ Public: Get schedules for a specific route
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<TrainSchedule>> getSchedulesByRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok(trainScheduleService.getSchedulesByRoute(routeId));
    }

    // 🔒 Admin-Only: Create a new train schedule
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<TrainSchedule> createSchedule(@RequestBody TrainSchedule schedule) {
        return ResponseEntity.ok(trainScheduleService.createSchedule(schedule));
    }

    // 🔒 Admin-Only: Update an existing train schedule
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<TrainSchedule> updateSchedule(@PathVariable UUID id, @RequestBody TrainSchedule updatedSchedule) {
        Optional<TrainSchedule> schedule = trainScheduleService.updateSchedule(id, updatedSchedule);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Delete a train schedule
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID id) {
        boolean deleted = trainScheduleService.deleteSchedule(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}