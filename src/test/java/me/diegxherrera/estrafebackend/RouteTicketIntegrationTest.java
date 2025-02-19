package me.diegxherrera.estrafebackend;

import me.diegxherrera.estrafebackend.model.*;
import me.diegxherrera.estrafebackend.repository.*;
import me.diegxherrera.estrafebackend.service.SeatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Ensures data persists across tests
// We removed the class-level @Transactional so that our setup data is committed.
public class RouteTicketIntegrationTest {

    @Autowired private RouteRepository routeRepository;
    @Autowired private TrainRepository trainRepository;
    @Autowired private CoachRepository coachRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private StationRepository stationRepository;
    @Autowired private TrainScheduleRepository trainScheduleRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private SeatService seatService;

    private static final String ROUTE_NAME = "Zurich - Berna";
    private UUID routeId;
    private final List<UUID> trainIds = new ArrayList<>();
    private final List<UUID> coachIds = new ArrayList<>();
    private final List<UUID> seatIds = new ArrayList<>();

    @BeforeAll
    @Commit  // Ensure that changes are committed so that later tests can see them.
    public void setupRouteAndTrain() {
        // --- Clear any previous test data ---
        trainScheduleRepository.deleteAll();
        coachRepository.deleteAll();
        seatRepository.deleteAll();
        routeRepository.deleteAll();
        stationRepository.deleteAll();
        cityRepository.deleteAll();
        trainRepository.deleteAll();

        // --- Now create fresh data ---
        // Step 1: Create Cities
        City zurich = new City("Zurich");
        City berna = new City("Berna");

        // Step 2: Create Stations and use addStation() to maintain bidirectional consistency
        Station zurichHb = new Station("Zurich HB", zurich);
        Station bernaHb = new Station("Berna HB", berna);
        zurich.addStation(zurichHb);
        berna.addStation(bernaHb);

        // Step 3: Save Cities (this cascades to save their Stations)
        zurich = cityRepository.save(zurich);
        berna = cityRepository.save(berna);

        // Step 4: Fetch persisted Stations (they are now managed)
        List<Station> persistedStations = stationRepository.findAll();

        // Step 5: Create and Save Route using the managed Station entities
        Route route = new Route();
        route.setName(ROUTE_NAME);
        route.setOriginStation(zurichHb);
        route.setDestinationStation(bernaHb);
        route.setStations(new HashSet<>(persistedStations));
        route = routeRepository.save(route);
        routeId = route.getId();

        // Step 6: Create a Train and save it
        Train train = new Train();
        train.setName("LAV 42");
        train.setService("High-Speed LAV");
        train.setNextStation("Zurich HB");
        train = trainRepository.save(train);
        trainIds.add(train.getId());

        // Step 7: Create a TrainSchedule for the train and route
        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrain(train);
        schedule.setRoute(route);
        schedule.setDepartureTime(LocalTime.of(7, 0));
        schedule.setArrivalTime(LocalTime.of(9, 30));
        schedule.setServiceDays("Mon-Sun");
        trainScheduleRepository.save(schedule);

        // Step 8: Create 2 Coaches with 10 Seats each
        for (int j = 1; j <= 2; j++) {
            Coach coach = new Coach();
            coach.setTrain(train);
            coach.setCoachNumber(j);
            coach.setCoachType(CoachType.REGULAR);
            coach = coachRepository.save(coach);
            coachIds.add(coach.getId());

            List<Seat> seats = new ArrayList<>();
            for (int k = 1; k <= 10; k++) {
                Seat seat = new Seat();
                seat.setSeatNumber("C" + j + "S" + k);
                seat.setClassType("Economy");
                seat.setBooked(false);
                seat.setCoach(coach);
                seats.add(seat);
            }
            seatRepository.saveAll(seats);
            seats.forEach(s -> seatIds.add(s.getId()));
        }

        // Optional: Validate creation counts
        assertEquals(1, trainRepository.count(), "Train count should be 1");
        assertEquals(2, coachRepository.count(), "Coach count should be 2");
        assertEquals(20, seatRepository.count(), "Seat count should be 20");
    }

    @Test
    @Order(1)
    public void clientBuysTicket() {
        assertFalse(coachIds.isEmpty(), "No coaches exist, ensure setupRouteAndTrain() ran successfully");

        UUID coachId = coachIds.get(0);
        Optional<Coach> coachOpt = coachRepository.findById(coachId);
        assertTrue(coachOpt.isPresent(), "Coach should exist");

        Coach coach = coachOpt.get();

        // Assign a seat to a client
        Optional<Seat> assignedSeat = seatService.assignSeat(coach.getId());
        assertTrue(assignedSeat.isPresent(), "Seat should be assigned");
        assertTrue(assignedSeat.get().isBooked(), "Seat should be marked as booked");

        // Validate available seat count decreases by one
        int availableSeats = seatService.countAvailableSeats(coach);
        assertEquals(9, availableSeats, "One seat should be booked, leaving 9 available.");
    }

    @Test
    @Order(2)
    public void clientCancelsTicket() {
        assertFalse(seatIds.isEmpty(), "No seats exist, ensure clientBuysTicket() ran successfully");

        UUID seatId = seatIds.get(0);
        Optional<Seat> seatOpt = seatService.unbookSeat(seatId);
        assertTrue(seatOpt.isPresent(), "Seat should exist");
        assertFalse(seatOpt.get().isBooked(), "Seat should be unbooked");

        // Validate available seat count increases back to 10 for the corresponding coach
        Coach coach = seatOpt.get().getCoach();
        int availableSeats = seatService.countAvailableSeats(coach);
        assertEquals(10, availableSeats, "All 10 seats should be available again.");
    }

    @Test
    @Order(3)
    public void cleanupTestData() {
        // Delete train schedules first to remove foreign-key dependencies
        trainScheduleRepository.deleteAll();

        // Then delete seats, coaches, train, and route in order
        seatRepository.deleteAllById(seatIds);
        coachRepository.deleteAllById(coachIds);
        trainRepository.deleteAllById(trainIds);
        routeRepository.deleteById(routeId);

        // Validate cleanup (if needed)
        assertEquals(0, trainRepository.count(), "Train count should be 0 after cleanup");
        assertEquals(0, coachRepository.count(), "Coach count should be 0 after cleanup");
        assertEquals(0, seatRepository.count(), "Seat count should be 0 after cleanup");
    }
}