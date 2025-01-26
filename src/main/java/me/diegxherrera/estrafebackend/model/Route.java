package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(mappedBy = "station", cascade = FetchType.LAZY)
}
