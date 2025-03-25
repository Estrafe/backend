package me.diegxherrera.estrafebackend.controller.v1;

import jakarta.transaction.Transactional;
import me.diegxherrera.estrafebackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TicketRepository ticketRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final CityRepository cityRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final StopScheduleRepository stopScheduleRepository;

    @Autowired
    public AdminController(TicketRepository ticketRepository,
                           CoachRepository coachRepository,
                           SeatRepository seatRepository,
                           TrainRepository trainRepository,
                           RouteRepository routeRepository,
                           StationRepository stationRepository,
                           CityRepository cityRepository,
                           TrainScheduleRepository trainScheduleRepository,
                           StopScheduleRepository stopScheduleRepository) {
        this.ticketRepository = ticketRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
        this.cityRepository = cityRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.stopScheduleRepository = stopScheduleRepository;
    }

    /**
     * Deletes all data from the database. The order of deletion is chosen to avoid
     * foreign key constraint issues.
     */
    @DeleteMapping("/deleteAllData")
    @Transactional
    public ResponseEntity<String> deleteAllData() {
        // Delete tickets first (they depend on other entities)
        ticketRepository.deleteAll();

        // Delete stop schedules
        stopScheduleRepository.deleteAll();

        // Delete train schedules (which reference trains)
        trainScheduleRepository.deleteAll();

        // Then delete coaches and seats (if not cascaded)
        coachRepository.deleteAll();
        seatRepository.deleteAll();

        // Now, delete trains
        trainRepository.deleteAll();

        // Delete routes (which reference stations)
        routeRepository.deleteAll();

        // Delete stations (which reference cities)
        stationRepository.deleteAll();

        // Finally, delete cities
        cityRepository.deleteAll();

        return ResponseEntity.ok("All data has been deleted successfully.");
    }
}