package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "route")
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


    public void setRouteName(String routeName) {
        this.name = routeName;
    }

    public String getRouteName() {
        return this.name;
    }
}
