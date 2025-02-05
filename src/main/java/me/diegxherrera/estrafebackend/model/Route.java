package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "route")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "route_station",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private Set<Station> stations = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "origin_station_id", nullable = false) // Foreign key to Station
    private Station originStation;

    @ManyToOne
    @JoinColumn(name = "destination_station_id", nullable = false) // Foreign key to Station
    private Station destinationStation;
}
