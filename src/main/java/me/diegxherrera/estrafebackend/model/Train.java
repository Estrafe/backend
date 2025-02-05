package me.diegxherrera.estrafebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "train")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String service; // e.g., Regional, Intercity, Link Express
    private String nextStation; // e.g., Zurich HB, Lugano Süd, Blenio HB

    @Version
    private Long version;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coach> coaches;

    public Train(String service, String name) {
        this.name = name;
        this.service = service;
    }
}