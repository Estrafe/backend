package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import org.apache.catalina.User;

import java.util.UUID;

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @OneToOne
    @JoinColumn(nullable = false, name = "seat_id") // The column name in the Ticket table referring to Seat
    private Seat seat;
}