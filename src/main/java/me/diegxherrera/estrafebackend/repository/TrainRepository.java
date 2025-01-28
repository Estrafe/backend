package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainRepository extends JpaRepository<Train, UUID> {
    // Additional custom query methods can be added here if needed
}