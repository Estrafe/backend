package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
}
