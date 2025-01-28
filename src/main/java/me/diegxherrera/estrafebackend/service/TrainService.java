package me.diegxherrera.estrafebackend.service;

import me.diegxherrera.estrafebackend.model.Train;
import me.diegxherrera.estrafebackend.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainService {

    @Autowired
    private TrainRepository trainRepository;

    // Retrieve all trains
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    // Retrieve a train by its ID
    public Optional<Train> getTrainById(UUID id) {
        return trainRepository.findById(id);
    }

    // Create a new train
    public Train createTrain(Train train) {
        return trainRepository.save(train);
    }

    // Update an existing train
    public Optional<Train> updateTrain(UUID id, Train updatedTrain) {
        return trainRepository.findById(id).map(existingTrain -> {
            existingTrain.setName(updatedTrain.getName());
            existingTrain.setService(updatedTrain.getService());
            existingTrain.setNextStation(updatedTrain.getNextStation());
            return trainRepository.save(existingTrain);
        });
    }

    // Delete a train by its ID
    public boolean deleteTrain(UUID id) {
        if (trainRepository.existsById(id)) {
            trainRepository.deleteById(id);
            return true;
        }
        return false;
    }
}