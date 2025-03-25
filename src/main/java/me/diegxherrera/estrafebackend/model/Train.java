package me.diegxherrera.estrafebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "train")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String service;
    private String nextStation;
    private LocalDateTime departureTime;

    // Rename columns to avoid reserved words
    @Column(name = "is_accessible", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean accessible;       // True if the train is accessible (for disabled)

    @Column(name = "is_co2_compliant", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean co2Compliant;     // True if train is CO2 compliant

    @Column(name = "is_animals_enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean animalsEnabled;   // True if animals (pets) are allowed

    @ManyToOne
    @JoinColumn(name = "route_id")
    @JsonIgnoreProperties({"stations", "originStation", "destinationStation"})
    private Route route;

    @Version
    private Long version;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // Serialize coaches from train side
    private List<Coach> coaches = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets = new HashSet<>();

    public Train(String service, String name) {
        this.name = name;
        this.service = service;
    }
}