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

    @Column(name = "booked", nullable = false)
    private boolean booked = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    public Seat(String s, String economy, Coach coach) {
        this.seatNumber = s;
        this.classType = economy;
        this.coach = coach;
        this.booked = false;
    }

    public void bookSeat() {
        this.booked = true;
    }

    public void unbookSeat() {
        this.booked = false;
    }
}