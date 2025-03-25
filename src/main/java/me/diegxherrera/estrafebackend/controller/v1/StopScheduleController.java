package me.diegxherrera.estrafebackend.controller.v1;

import me.diegxherrera.estrafebackend.model.StopSchedule;
import me.diegxherrera.estrafebackend.service.StopScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/stops")
public class StopScheduleController {

    private final StopScheduleService stopScheduleService;

    public StopScheduleController(StopScheduleService stopScheduleService) {
        this.stopScheduleService = stopScheduleService;
    }

    // ✅ Public: Get all stops for a specific train schedule
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<StopSchedule>> getStopsBySchedule(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(stopScheduleService.getStopsBySchedule(scheduleId));
    }

    // ✅ Public: Get a specific stop schedule by ID
    @GetMapping("/{id}")
    public ResponseEntity<StopSchedule> getStopById(@PathVariable UUID id) {
        Optional<StopSchedule> stop = stopScheduleService.getStopById(id);
        return stop.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Create a new stop schedule
    @Secured("ROLE_ADMIN")
    @PostMapping("/schedule/{scheduleId}")
    public ResponseEntity<StopSchedule> createStopSchedule(@PathVariable UUID scheduleId, @RequestBody StopSchedule stop) {
        return ResponseEntity.ok(stopScheduleService.createStopSchedule(scheduleId, stop));
    }

    // 🔒 Admin-Only: Update an existing stop schedule
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<StopSchedule> updateStopSchedule(@PathVariable UUID id, @RequestBody StopSchedule updatedStop) {
        Optional<StopSchedule> stop = stopScheduleService.updateStopSchedule(id, updatedStop);
        return stop.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Admin-Only: Delete a stop schedule
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStopSchedule(@PathVariable UUID id) {
        boolean deleted = stopScheduleService.deleteStopSchedule(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}