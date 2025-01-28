package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "train_schedule")
public class TrainSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY) // Each schedule corresponds to one train
    private Train train; // Reference to the Train entity

    private LocalTime departureTime; // From the starting station (e.g., Lugano Süd)
    private LocalTime arrivalTime;   // To the ending station (e.g., Andermatt)

    private String serviceDays; // Days of service, e.g., Mon-Fri

    @ManyToOne(fetch = FetchType.LAZY) // Many schedules can belong to one route
    private Route route;

    @OneToMany(mappedBy = "trainSchedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StopSchedule> stopSchedules; // Stops along the route
}