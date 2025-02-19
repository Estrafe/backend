package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Coach;
import me.diegxherrera.estrafebackend.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoachRepository extends JpaRepository<Coach, UUID> {

    // ✅ Find all coaches for a specific train
    List<Coach> findByTrain(Train train);

    // ✅ Alternative: Find coaches by train ID (more efficient)
    List<Coach> findByTrainId(UUID trainId);
}