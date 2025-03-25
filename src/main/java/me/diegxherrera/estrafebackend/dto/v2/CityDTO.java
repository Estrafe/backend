package me.diegxherrera.estrafebackend.dto.v2;

import me.diegxherrera.estrafebackend.model.Station;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CityDTO {
    private UUID id;
    private String name;
    private Set<Station> stations = new HashSet<>();

}
