package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "train")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String service; // e.g., Regional, Intercity, Link Express
    private Date nextStation; // e.g., Zurich HB, Lugano Süd, Blenio HB

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Seat> seats;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Coach> coaches;

    // Getters and Setters
}