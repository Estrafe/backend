package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoachRepository extends JpaRepository<Coach, UUID> {
    // Custom query methods (if needed)
}