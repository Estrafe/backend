package me.diegxherrera.estrafebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "stop_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    public StopSchedule(String stopName, LocalTime scheduledArrival, LocalTime scheduledDeparture,
                        LocalTime actualArrival, LocalTime actualDeparture, TrainSchedule trainSchedule) {
        this.stopName = stopName;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.actualArrival = actualArrival;
        this.actualDeparture = actualDeparture;
        this.trainSchedule = trainSchedule;
    }
}