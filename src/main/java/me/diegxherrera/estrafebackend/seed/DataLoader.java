package me.diegxherrera.estrafebackend.seed;

import jakarta.transaction.Transactional;
import me.diegxherrera.estrafebackend.model.*;
import me.diegxherrera.estrafebackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * A combined data loader that avoids duplicates by checking if a city/station
 * already exists before creating a new one, and that sets up multiple routes,
 * trains, coaches, seats, etc.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final StopScheduleRepository stopScheduleRepository;
    private final TicketRepository ticketRepository;

    private final Random random = new Random();

    public DataLoader(CityRepository cityRepository,
                      StationRepository stationRepository,
                      RouteRepository routeRepository,
                      TrainRepository trainRepository,
                      CoachRepository coachRepository,
                      SeatRepository seatRepository,
                      TrainScheduleRepository trainScheduleRepository,
                      StopScheduleRepository stopScheduleRepository, TicketRepository ticketRepository) {
        this.cityRepository = cityRepository;
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
        this.trainRepository = trainRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.stopScheduleRepository = stopScheduleRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("=== Starting DataLoader ===");

        // 1) Create or find the cities (10 total).
        City zurich    = findOrCreateCity("Zurich");
        City geneva    = findOrCreateCity("Geneva");
        City basel     = findOrCreateCity("Basel");
        City bern      = findOrCreateCity("Bern");
        City lugano    = findOrCreateCity("Lugano");
        City lausanne  = findOrCreateCity("Lausanne");
        City stGallen  = findOrCreateCity("St. Gallen");
        City winterthur= findOrCreateCity("Winterthur");
        City interlaken= findOrCreateCity("Interlaken");
        City chur      = findOrCreateCity("Chur");

        // 2) Create or find the stations (1 per city).
        Station zurichHB        = findOrCreateStation("Zurich HB",         zurich);
        Station genevaAirport   = findOrCreateStation("Geneva Airport",    geneva);
        Station baselSBB        = findOrCreateStation("Basel SBB",         basel);
        Station bernHB          = findOrCreateStation("Bern HB",           bern);
        Station luganoHB        = findOrCreateStation("Lugano HB",         lugano);
        Station lausanneFL      = findOrCreateStation("Lausanne FL",       lausanne);
        Station stGallenCentral = findOrCreateStation("St. Gallen Central",stGallen);
        Station winterthurCentral=findOrCreateStation("Winterthur Central",winterthur);
        Station interlakenWest  = findOrCreateStation("Interlaken West",   interlaken);
        Station churCentral     = findOrCreateStation("Chur Central",      chur);

        // 3) Create routes (some direct, some multi-stop).
        // Direct routes:
        Route routeZG = createOrFindRoute("Zurich to Geneva", zurichHB, genevaAirport);
        Route routeBL = createOrFindRoute("Basel to Lugano", baselSBB, luganoHB);
        Route routeBW = createOrFindRoute("Bern to Winterthur", bernHB, winterthurCentral);
        Route routeIC = createOrFindRoute("Interlaken to Chur", interlakenWest, churCentral);

        // Multi-stop routes:
        Route routeZB = createOrFindMultiStopRoute(
                "Zurich -> Basel -> Bern",
                Arrays.asList(zurichHB, baselSBB, bernHB)
        );
        Route routeGL = createOrFindMultiStopRoute(
                "Geneva -> Lausanne -> St. Gallen",
                Arrays.asList(genevaAirport, lausanneFL, stGallenCentral)
        );

        // 4) Define departure times, date range, and create trains.
        LocalTime[] departureTimes = {
                LocalTime.of(6, 0),
        };
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate   = startDate.plusDays(13); // 14 days total

        // Create trains (and schedules) for each route.
        createTrainsForRoute(routeZG, zurichHB, genevaAirport,   startDate, endDate, departureTimes, "Express");
        createTrainsForRoute(routeBL, baselSBB, luganoHB,        startDate, endDate, departureTimes, "Intercity");
        createTrainsForRoute(routeBW, bernHB, winterthurCentral, startDate, endDate, departureTimes, "Regional");
        createTrainsForRoute(routeIC, interlakenWest, churCentral, startDate, endDate, departureTimes, "Scenic");
        createTrainsForRoute(routeZB, zurichHB, bernHB,          startDate, endDate, departureTimes, "Express");
        createTrainsForRoute(routeGL, genevaAirport, stGallenCentral, startDate, endDate, departureTimes, "Intercity");

        // 5) For each train, create coaches and seats.
        for (Train train : trainRepository.findAll()) {
            // Randomize number of coaches: 2 to 4.
            int numCoaches = 2 + random.nextInt(2);
            for (int i = 1; i <= numCoaches; i++) {
                Coach coach = new Coach();
                coach.setCoachNumber(i);
                coach.setTrain(train);
                // Randomly choose REGULAR or FIRST_CLASS.
                coach.setCoachType(random.nextBoolean() ? CoachType.REGULAR : CoachType.FIRST_CLASS);
                coach = coachRepository.save(coach);

                // Random seats: 40 to 60.
                int numSeats = 1 + random.nextInt(2);
                List<Seat> seats = new ArrayList<>();
                for (int j = 1; j <= numSeats; j++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber("C" + i + "-S" + j);
                    seat.setClassType(coach.getCoachType() == CoachType.FIRST_CLASS ? "First" : "Economy");
                    seat.setBooked(false);
                    seat.setCoach(coach);
                    seat = seatRepository.save(seat);
                    seats.add(seat);
                }
                coach.setSeats(seats);
                coachRepository.save(coach);
                train.getCoaches().add(coach);
            }
            trainRepository.save(train);
        }
        generateTicketsForSchedules();
        System.out.println("=== Extended complex data loaded successfully ===");
    }

    /**
     * 🎟️ Generates tickets for each seat in every train schedule.
     */
    private void generateTicketsForSchedules() {
        List<TrainSchedule> schedules = trainScheduleRepository.findAll();
        int ticketCount = 0;

        for (TrainSchedule schedule : schedules) {
            List<Seat> seats = seatRepository.findByTrain(schedule.getTrain()); // ✅ Use the new method!

            for (Seat seat : seats) {
                if (!ticketRepository.existsByScheduleAndSeat(schedule, seat)) { // ✅ Use the new method!
                    Ticket ticket = new Ticket();
                    ticket.setSchedule(schedule);
                    ticket.setSeat(seat);
                    ticket.setCoach(seat.getCoach());
                    ticket.setTrain(schedule.getTrain());
                    ticket.setUsername(null);
                    ticket.setStatus(Ticket.TicketStatus.AVAILABLE); // 🔥 Ensure this enum exists

                    ticketRepository.save(ticket);
                    ticketCount++;
                }
            }
        }
        System.out.println("✅ Generated " + ticketCount + " tickets for train schedules.");
    }
    // --- Helper Methods ---

    /**
     * Find or create a City by name.
     * This method uses a custom query (or method) findByName in your CityRepository
     * which must return Optional<City> or City if you have that method defined.
     */
    private City findOrCreateCity(String name) {
        Optional<City> existing = cityRepository.findByName(name);
        if (existing.isPresent()) {
            System.out.println("City already exists: " + name + " (id=" + existing.get().getId() + ")");
            return existing.get();
        } else {
            City city = new City();
            city.setName(name);
            city = cityRepository.save(city);
            System.out.println("Created new city: " + name + " (id=" + city.getId() + ")");
            return city;
        }
    }

    /**
     * Find or create a Station by name+city.
     * You might need a custom method in StationRepository, e.g.:
     *   Optional<Station> findByNameAndCity(String name, City city);
     */
    private Station findOrCreateStation(String stationName, City city) {
        // For example, if your StationRepository has findByNameAndCity:
        Optional<Station> existing = stationRepository.findByNameAndCity(stationName, city);
        if (existing.isPresent()) {
            System.out.println("Station already exists: " + stationName
                    + " in city " + city.getName()
                    + " (id=" + existing.get().getId() + ")");
            return existing.get();
        } else {
            Station station = new Station();
            station.setName(stationName);
            station.setCity(city);
            station = stationRepository.save(station);
            System.out.println("Created new station: " + stationName
                    + " in city " + city.getName()
                    + " (id=" + station.getId() + ")");
            return station;
        }
    }

    /**
     * Creates or finds a direct route (2 stations: origin, destination).
     * If you have a unique constraint on route name, or on (origin, destination),
     * you can check that here. For example, routeRepository.findByName(...).
     */
    private Route createOrFindRoute(String name, Station origin, Station destination) {
        // If you have routeRepository.findByName(...) or findByOriginStationAndDestinationStation, do that first:
        Optional<Route> existing = routeRepository.findByName(name);
        if (existing.isPresent()) {
            System.out.println("Route already exists: " + name + " (id=" + existing.get().getId() + ")");
            return existing.get();
        } else {
            Route route = new Route();
            route.setName(name);
            route.setOriginStation(origin);
            route.setDestinationStation(destination);
            route.getStations().add(origin);
            route.getStations().add(destination);
            route = routeRepository.save(route);
            System.out.println("Created new route: " + name + " (id=" + route.getId() + ")");
            return route;
        }
    }

    /**
     * Creates or finds a multi-stop route.
     * The route name plus the list of stations can be used to detect duplicates if needed.
     */
    private Route createOrFindMultiStopRoute(String name, List<Station> stations) {
        // Check if route with the same name already exists:
        Optional<Route> existing = routeRepository.findByName(name);
        if (existing.isPresent()) {
            System.out.println("Route already exists: " + name + " (id=" + existing.get().getId() + ")");
            return existing.get();
        } else {
            if (stations.size() < 2) {
                throw new IllegalArgumentException("Need at least 2 stations for a multi-stop route!");
            }
            Route route = new Route();
            route.setName(name);
            route.setOriginStation(stations.get(0));
            route.setDestinationStation(stations.get(stations.size()-1));
            // Add all stations in order:
            for (Station st : stations) {
                route.getStations().add(st);
            }
            route = routeRepository.save(route);
            System.out.println("Created new multi-stop route: " + name + " (id=" + route.getId() + ")");
            return route;
        }
    }

    /**
     * Creates trains and schedules for a route over a date range, with a given service type.
     */
    private void createTrainsForRoute(Route route,
                                      Station origin,
                                      Station destination,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LocalTime[] times,
                                      String serviceType) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (int i = 0; i < times.length; i++) {
                // 1) Create the Train entity
                Train train = new Train();
                train.setName(route.getName() + " Train " + (i + 1));
                train.setService(serviceType);
                train.setNextStation(destination.getName());
                train.setAccessible(random.nextBoolean());
                train.setCo2Compliant(random.nextBoolean());
                train.setAnimalsEnabled(random.nextBoolean());

                LocalDateTime departureDateTime = LocalDateTime.of(date, times[i]);
                train.setDepartureTime(departureDateTime);
                train.setRoute(route);
                train = trainRepository.save(train);

                // 2) Create the TrainSchedule
                TrainSchedule schedule = new TrainSchedule();
                schedule.setTrain(train);
                schedule.setDepartureTime(departureDateTime);

                // If multi-stop route, assume 3 hours, else 2 hours
                int travelHours = route.getStations().size() > 2 ? 3 : 2;
                LocalDateTime arrivalDateTime = departureDateTime.plusHours(travelHours);
                schedule.setArrivalTime(arrivalDateTime);
                schedule.setServiceDays("Daily");
                schedule.setRoute(route);

                // Random base price between 30 and 150
                int basePrice = 30 + random.nextInt(121);
                schedule.setBasePrice(basePrice);
                schedule = trainScheduleRepository.save(schedule);

                // 3) Create StopSchedules for each station in route
                for (Station st : route.getStations()) {
                    StopSchedule stop = new StopSchedule();
                    stop.setStopName(st.getName());

                    if (st.equals(route.getOriginStation())) {
                        // departure
                        stop.setScheduledDeparture(departureDateTime.toLocalTime());
                        stop.setScheduledArrival(departureDateTime.toLocalTime());
                        stop.setActualDeparture(departureDateTime.toLocalTime());
                        stop.setActualArrival(departureDateTime.toLocalTime());
                    } else if (st.equals(route.getDestinationStation())) {
                        // final arrival
                        stop.setScheduledArrival(arrivalDateTime.toLocalTime());
                        stop.setScheduledDeparture(arrivalDateTime.toLocalTime());
                        stop.setActualArrival(arrivalDateTime.toLocalTime());
                        stop.setActualDeparture(arrivalDateTime.toLocalTime());
                    } else {
                        // intermediate
                        LocalDateTime intermediateTime = departureDateTime.plusHours(1);
                        stop.setScheduledArrival(intermediateTime.toLocalTime());
                        stop.setScheduledDeparture(intermediateTime.toLocalTime());
                        stop.setActualArrival(intermediateTime.toLocalTime());
                        stop.setActualDeparture(intermediateTime.toLocalTime());
                    }
                    stop.setTrainSchedule(schedule);
                    stopScheduleRepository.save(stop);
                }
            }
        }
    }
}