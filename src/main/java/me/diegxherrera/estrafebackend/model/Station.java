package me.diegxherrera.estrafebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToMany(mappedBy = "stations")
    @JsonIgnore  // Prevents deep JSON nesting issues
    private Set<Route> routes = new HashSet<>();

    // New Field: Tracks tickets sold at this station
    @Column(nullable = false)
    private int ticketsSold = 0;

    public Station(String name, City city) {
        this.name = name;
        this.city = city;
        this.ticketsSold = 0;
    }

    // Method to update ticket sales count
    public void sellTickets(int quantity) {
        if (quantity > 0) {
            this.ticketsSold += quantity;
        }
    }
}