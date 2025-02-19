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
@Table(name = "station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id")
    private City city;

    // Inverse side: mappedBy refers to the property name in Route (which is "stations")
    @ManyToMany(mappedBy = "stations", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Route> routes = new HashSet<>();

    public Station(String name, City city) {
        this.name = name;
        this.city = city;
    }
}