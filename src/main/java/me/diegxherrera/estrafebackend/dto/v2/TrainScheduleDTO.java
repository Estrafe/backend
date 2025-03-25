package me.diegxherrera.estrafebackend.dto.v2;

import lombok.Getter;
import lombok.Setter;
import me.diegxherrera.estrafebackend.model.Train;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class TrainScheduleDTO {
    private UUID id;
    private Train train;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double basePrice;
}
