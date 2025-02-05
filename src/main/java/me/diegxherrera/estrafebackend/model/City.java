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
@Table(name = "city")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Station> stations = new HashSet<>();

    public void addStation(Station station) {
        this.stations.add(station);
    }

    public void removeStation(Station station) {
        this.stations.remove(station);
    }

    public City(String name) {
        this.name = name;
    }
}