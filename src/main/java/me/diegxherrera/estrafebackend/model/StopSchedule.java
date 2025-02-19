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
    private String stopName;

    private LocalTime scheduledArrival;
    private LocalTime scheduledDeparture;
    private LocalTime actualArrival;
    private LocalTime actualDeparture;

    @ManyToOne
    @JoinColumn(name = "train_schedule_id", nullable = false)
    private TrainSchedule trainSchedule;

    // Minimal constructor
    public StopSchedule(
            String stopName,
            LocalTime scheduledArrival,
            LocalTime scheduledDeparture,
            LocalTime actualArrival,
            LocalTime actualDeparture,
            TrainSchedule trainSchedule
    ) {
        this.stopName = stopName;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.actualArrival = actualArrival;
        this.actualDeparture = actualDeparture;
        this.trainSchedule = trainSchedule;
    }
}