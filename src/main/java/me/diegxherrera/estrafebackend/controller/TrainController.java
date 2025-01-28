package me.diegxherrera.estrafebackend.controller;

import me.diegxherrera.estrafebackend.model.Train;
import me.diegxherrera.estrafebackend.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    // Get all trains
    @GetMapping
    public ResponseEntity<List<Train>> getAllTrains() {
        List<Train> trains = trainService.getAllTrains();
        return ResponseEntity.ok(trains);
    }

    // Get train by ID
    @GetMapping("/{id}")
    public ResponseEntity<Train> getTrainById(@PathVariable UUID id) {
        Optional<Train> train = trainService.getTrainById(id);
        return train.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new train
    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody Train train) {
        Train createdTrain = trainService.createTrain(train);
        return ResponseEntity.ok(createdTrain);
    }

    // Update an existing train
    @PutMapping("/{id}")
    public ResponseEntity<Train> updateTrain(@PathVariable UUID id, @RequestBody Train updatedTrain) {
        Optional<Train> train = trainService.updateTrain(id, updatedTrain);
        return train.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a train
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable UUID id) {
        boolean deleted = trainService.deleteTrain(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}