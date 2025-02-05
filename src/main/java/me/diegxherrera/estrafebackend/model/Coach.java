package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int coachNumber; // Unique coach number within the train

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats; // List of seats in the coach

    // ✅ Constructor with parameters
    public Coach(Train train, int coachNumber) {
        this.train = train;
        this.coachNumber = coachNumber;
    }
}