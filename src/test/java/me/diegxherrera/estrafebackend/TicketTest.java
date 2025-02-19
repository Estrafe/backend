package me.diegxherrera.estrafebackend;

import me.diegxherrera.estrafebackend.model.*;
import org.springframework.transaction.annotation.Transactional; // use Spring's annotation
import me.diegxherrera.estrafebackend.repository.CoachRepository;
import me.diegxherrera.estrafebackend.repository.SeatRepository;
import me.diegxherrera.estrafebackend.repository.TicketRepository;
import me.diegxherrera.estrafebackend.repository.TrainRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Ensure data persists across test cases
// Do not use a class-level @Transactional here so that the setup commits.
public class TicketTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private UUID ticketId;

    @BeforeEach
    @Transactional  // Use Spring's transaction management
    @Commit          // Commit changes so that the web layer sees the data
    public void setUp() {
        // Clear existing data
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        coachRepository.deleteAll();
        trainRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create & save Train
        Train train = new Train("High-Speed", "AVE 1234");
        train = trainRepository.saveAndFlush(train);

        // Create & save Coach
        Coach coach = new Coach(train, 1, CoachType.REGULAR);
        coach = coachRepository.saveAndFlush(coach);

        // Create & save Seat
        Seat seat = new Seat("12A", "Economy", coach);
        seat = seatRepository.saveAndFlush(seat);

        // Create & save Ticket
        Ticket ticket = new Ticket(UUID.randomUUID(), seat, coach, train, "GibraltarUser123");
        ticket = ticketRepository.saveAndFlush(ticket);

        // Flush and clear to ensure data is committed
        entityManager.flush();
        entityManager.clear();

        // Store the Ticket ID for test usage
        ticketId = ticket.getId();
    }

    @AfterEach
    @Transactional  // Run cleanup within a transaction
    @Commit         // Commit the deletion
    public void tearDown() {
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        coachRepository.deleteAll();
        trainRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testDownloadTicketPDF() throws Exception {
        mockMvc.perform(get("/api/tickets/" + ticketId + "/export")
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}