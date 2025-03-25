package me.diegxherrera.estrafebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int coachNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    @JsonBackReference  // Prevent infinite recursion with Train
    private Train train;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoachType coachType;

    public Coach(Train train, int coachNumber, CoachType coachType) {
        this.train = train;
        this.coachNumber = coachNumber;
        this.coachType = coachType;
    }
}