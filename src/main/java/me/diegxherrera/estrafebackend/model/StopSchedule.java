package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "stop_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StopSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String stopName;          // e.g., Lugano HB, Latz HB

    private LocalTime scheduledArrival; // ETA at this stop
    private LocalTime scheduledDeparture; // Departure time from this stop
    private LocalTime actualArrival;  // Real-time ATA
    private LocalTime actualDeparture; // Real-time departure time

    @ManyToOne
    private TrainSchedule trainSchedule;
}