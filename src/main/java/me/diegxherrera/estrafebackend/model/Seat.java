package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

    private String seatNumber;
    private String classType; // e.g., Economy, Comfort, Business

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    public Seat(String seatNumber, String classType, Coach coach) {
        this.seatNumber = seatNumber;
        this.classType = classType;
        this.coach = coach;
    }

    public Seat() {
    }
}