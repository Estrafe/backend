package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
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
    private String nextStation; // e.g., Zurich HB, Lugano Süd, Blenio HB

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coach> coaches;

    // Getters & Setters

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setNextStation(String nextStation) {
        this.nextStation = nextStation;
    }

    public String getNextStation() {
        return nextStation;
    }

    // --

    public Train(String service, String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.service = service;
    }

    public Train() {
        this.id = UUID.randomUUID();
    }

}