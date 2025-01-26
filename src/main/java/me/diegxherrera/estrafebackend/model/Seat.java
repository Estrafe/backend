package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import me.diegxherrera.estrafebackend.model.Train;

@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    private String classType; // e.g., Economy, Comfort, Business

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

}