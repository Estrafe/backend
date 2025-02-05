package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
