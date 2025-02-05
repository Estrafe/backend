package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "seat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String seatNumber; // Unique seat identifier within the coach
    private String classType;  // e.g., Economy, Comfort, Business
    private boolean isBooked = false; // Indicates if the seat is booked

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    public void bookSeat() {
        this.isBooked = true;
    }

    public void unbookSeat() {
        this.isBooked = false;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", seatNumber='" + seatNumber + '\'' +
                ", classType='" + classType + '\'' +
                ", isBooked=" + isBooked +
                ", coach=" + (coach != null ? coach.getCoachNumber() : "null") +
                '}';
    }
}